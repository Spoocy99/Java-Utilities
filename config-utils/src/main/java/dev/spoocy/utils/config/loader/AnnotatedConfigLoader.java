package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.*;
import dev.spoocy.utils.config.Readable;
import dev.spoocy.utils.reflection.ClassWalker;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.Accessor;
import dev.spoocy.utils.reflection.accessor.ConstructorAccessor;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Binds config values to annotated classes using reflection.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class AnnotatedConfigLoader {

    private static final ConcurrentMap<Class<?>, BoundType<?>> TYPES = new ConcurrentHashMap<>();

    public AnnotatedConfigLoader() {
    }

    public <T> T load(@NotNull Class<T> type, @NotNull Readable readable) {
        return loadResult(type, readable).instance();
    }

    @NotNull
    public <T> LoadResult<T> loadResult(@NotNull Class<T> type, @NotNull Readable readable) {
        Args.notNull(type, "type");
        return bind(describe(type), readable);
    }

    @SuppressWarnings("unchecked")
    public <T> int write(@NotNull T instance, @NotNull Writeable writeable) {
        Args.notNull(instance, "instance");
        Args.notNull(writeable, "writeable");

        BoundType<T> boundType = describe((Class<T>) instance.getClass());
        return writeFieldsToConfig(boundType, instance, writeable, resolveBasePath(boundType.source()));
    }

    @NotNull
    private <T> LoadResult<T> bind(@NotNull BoundType<T> boundType, @NotNull Readable readable) {
        Args.notNull(boundType, "boundType");
        Args.notNull(readable, "readable");

        T instance = createInstance(boundType);
        String basePath = resolveBasePath(boundType.source());
        int mutations = 0;

        invokePreHooks(boundType.preHooks(), instance, readable);

        for (BoundField field : boundType.fields()) {
            ConfigProperty property = field.property();
            String rawPath = resolvePropertyPath(field);
            String fullPath = combinePath(basePath, rawPath);
            boolean shouldPersistField = shouldSaveDefault(boundType.source(), property);

            Object current = field.get(instance);
            Object loaded = resolveValue(readable, fullPath, field, current);

            if (loaded == null) {
                if (shouldPersistField && current != null && !readable.isSet(fullPath) && readable instanceof Writeable) {
                    ((Writeable) readable).set(fullPath, current);
                    mutations++;
                }
            } else {
                field.set(instance, loaded);
            }

            if (shouldPersistField) {
                mutations += applyPropertyComments(readable, fullPath, property);
            }
        }

        mutations += applySourceComments(boundType.source(), readable, basePath);

        PostLoadResult postLoadResult = invokePostHooks(boundType.postHooks(), instance, readable);
        if (postLoadResult == PostLoadResult.SAVE && readable instanceof Writeable) {
            mutations += writeFieldsToConfig(boundType, instance, (Writeable) readable, basePath);
        }

        return new LoadResult<>(instance, mutations);
    }

    /**
     * Writes all bound field values of {@code instance} into {@code writeable} and returns
     * the number of fields written (each counts as one mutation).
     */
    private static <T> int writeFieldsToConfig(
            @NotNull BoundType<T> boundType,
            @NotNull T instance,
            @NotNull Writeable writeable,
            @NotNull String basePath
    ) {
        int count = 0;
        for (BoundField field : boundType.fields()) {
            if (!shouldWriteField(boundType.source(), field.property())) {
                continue;
            }

            String fullPath = combinePath(basePath, resolvePropertyPath(field));
            Object value = field.get(instance);
            if (value != null) {
                writeable.set(fullPath, value);
                count++;
            }
        }
        return count;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private static <T> BoundType<T> describe(@NotNull Class<T> type) {
        return (BoundType<T>) TYPES.computeIfAbsent(type, AnnotatedConfigLoader::createBoundType);
    }

    @NotNull
    private static <T> BoundType<T> createBoundType(@NotNull Class<T> type) {
        return new BoundType<>(
                type,
                Reflection.getAnnotation(type, ConfigSource.class, false),
                resolveConstructor(type),
                List.copyOf(resolveFields(type)),
                List.copyOf(resolveHooks(type, PreLoad.class)),
                List.copyOf(resolveHooks(type, PostLoad.class))
        );
    }

    public static final class LoadResult<T> {

        private final T instance;
        private final int mutations;

        private LoadResult(@NotNull T instance, int mutations) {
            this.instance = instance;
            this.mutations = mutations;
        }

        @NotNull
        public T instance() {
            return this.instance;
        }

        public int mutations() {
            return this.mutations;
        }
    }

    @NotNull
    private static <T> ConstructorAccessor resolveConstructor(@NotNull Class<T> type) {
        ConstructorAccessor accessor = Reflection.builder()
                .forClass(type)
                .privateMembers()
                .buildAccess()
                .constructor();

        if (accessor == null) {
            throw new IllegalArgumentException("Class must have a no-args constructor: " + type.getName());
        }

        return accessor;
    }

    @NotNull
    private static List<BoundField> resolveFields(@NotNull Class<?> type) {
        List<BoundField> fields = new ArrayList<>();

        for (Class<?> current : ClassWalker.walk(type)) {
            if (current == Object.class || current.isInterface()) {
                continue;
            }

            for (Field field : current.getDeclaredFields()) {
                BoundField boundField = BoundField.of(field);
                if (boundField != null) {
                    fields.add(boundField);
                }
            }
        }

        return fields;
    }

    @NotNull
    private static List<BoundHook> resolveHooks(
            @NotNull Class<?> type,
            @NotNull Class<? extends java.lang.annotation.Annotation> annotation
    ) {
        List<BoundHook> hooks = new ArrayList<>();

        for (Class<?> current : ClassWalker.walk(type)) {
            if (current == Object.class || current.isInterface()) {
                continue;
            }

            for (Method method : current.getDeclaredMethods()) {
                BoundHook hook = BoundHook.of(method, annotation);
                if (hook != null) {
                    hooks.add(hook);
                }
            }
        }

        return hooks;
    }

    private static void invokePreHooks(
            @NotNull List<BoundHook> hooks,
            @NotNull Object instance,
            @NotNull Readable readable
    ) {
        for (BoundHook hook : hooks) {
            hook.invoke(instance, readable);
        }
    }

    @NotNull
    private static PostLoadResult invokePostHooks(
            @NotNull List<BoundHook> hooks,
            @NotNull Object instance,
            @NotNull Readable readable
    ) {
        PostLoadResult result = PostLoadResult.NONE;
        for (BoundHook hook : hooks) {
            PostLoadResult hookResult = hook.invoke(instance, readable);
            if (hookResult.ordinal() > result.ordinal()) {
                result = hookResult;
            }
        }
        return result;
    }

    @NotNull
    private static String resolveBasePath(@Nullable ConfigSource source) {
        return source == null ? "" : source.section()
                .trim();
    }

    private static boolean shouldSaveDefaults(@Nullable ConfigSource source) {
        return source != null && source.saveDefaults();
    }

    private static boolean shouldSaveDefault(@Nullable ConfigSource source, @Nullable ConfigProperty property) {
        if (property != null) {
            return property.saveDefault() && shouldSaveDefaults(source);
        }
        return shouldSaveDefaults(source);
    }

    private static boolean shouldWriteField(@Nullable ConfigSource source, @Nullable ConfigProperty property) {
        return shouldSaveDefault(source, property);
    }

    @NotNull
    private static String resolvePropertyPath(@NotNull BoundField field) {
        ConfigProperty property = field.property();
        if (property == null || property.value()
                .trim()
                .isEmpty()) {
            return field.name();
        }
        return property.value()
                .trim();
    }

    @NotNull
    private static String combinePath(@NotNull String basePath, @NotNull String path) {
        if (basePath.isEmpty()) {
            return path;
        }
        if (path.isEmpty()) {
            return basePath;
        }
        return basePath + '.' + path;
    }

    @NotNull
    private static <T> T createInstance(@NotNull BoundType<T> boundType) {
        try {
            return boundType.type()
                    .cast(boundType.constructor()
                            .invoke());
        } catch (Exception e) {
            throw new IllegalArgumentException("Class must have a no-args constructor: " + boundType.type()
                    .getName(), e);
        }
    }

    private static int applySourceComments(
            @Nullable ConfigSource source,
            @NotNull Readable readable,
            @NotNull String basePath
    ) {
        if (!(readable instanceof Commentable)) {
            return 0;
        }

        if (source == null) {
            return 0;
        }

        Commentable commentable = (Commentable) readable;
        int mutations = 0;

        mutations += updateComments(commentable.getHeaderComments(), toComments(source.headerComments()), commentable::setHeaderComments);
        mutations += updateComments(commentable.getFooterComments(), toComments(source.footerComments()), commentable::setFooterComments);

        if (!basePath.isEmpty() && ensurePathExists(readable, basePath)) {
            mutations += updateComments(commentable.getComments(basePath), toComments(source.comments()), comments -> commentable.setComments(basePath, comments));
            mutations += updateComments(commentable.getInlineComments(basePath), toComments(source.inlineComments()), comments -> commentable.setInlineComments(basePath, comments));
        }

        return mutations;
    }

    private static int applyPropertyComments(
            @NotNull Readable readable,
            @NotNull String fullPath,
            @Nullable ConfigProperty property
    ) {
        if (!(readable instanceof Commentable) || property == null || !ensurePathExists(readable, fullPath)) {
            return 0;
        }

        Commentable commentable = (Commentable) readable;
        int mutations = 0;

        mutations += updateComments(commentable.getComments(fullPath), toComments(property.comments()), comments -> commentable.setComments(fullPath, comments));
        mutations += updateComments(commentable.getInlineComments(fullPath), toComments(property.inlineComments()), comments -> commentable.setInlineComments(fullPath, comments));

        return mutations;
    }

    private static boolean ensurePathExists(@NotNull Readable readable, @NotNull String path) {
        if (path.isEmpty() || readable.isSet(path)) {
            return true;
        }

        if (!(readable instanceof ConfigSection)) {
            return false;
        }

        ConfigSection root = (ConfigSection) readable;
        char separator = root.getRoot()
                .settings()
                .pathSeparator();
        String[] parts = path.split(java.util.regex.Pattern.quote(String.valueOf(separator)));
        ConfigSection current = root;

        for (int index = 0; index < parts.length - 1; index++) {
            String part = parts[index];
            if (part.isEmpty()) {
                continue;
            }

            ConfigSection next = current.getSection(part);
            if (next == null) {
                if (current.isSet(part)) {
                    return false;
                }
                next = current.createSection(part);
            }
            current = next;
        }

        return readable.isSet(path);
    }

    @NotNull
    private static List<String> toComments(@NotNull String[] comments) {
        if (comments.length == 0) {
            return Collections.emptyList();
        }

        List<String> lines = new ArrayList<>(comments.length);
        Collections.addAll(lines, comments);
        return Collections.unmodifiableList(lines);
    }

    private static int updateComments(
            @NotNull List<String> current,
            @NotNull List<String> desired,
            @NotNull CommentSetter setter
    ) {
        if (desired.isEmpty() || Objects.equals(current, desired)) {
            return 0;
        }

        setter.apply(desired);
        return 1;
    }

    @FunctionalInterface
    private interface CommentSetter {

        void apply(@NotNull List<String> comments);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static Object resolveValue(
            @NotNull Readable readable,
            @NotNull String path,
            @NotNull BoundField field,
            @Nullable Object defaultValue
    ) {
        Class<?> fieldType = field.type();
        if (!readable.isSet(path)) {
            return null;
        }

        if (fieldType == String.class) {
            return readable.getString(path, defaultValue instanceof String ? (String) defaultValue : "");
        }
        if (fieldType == int.class || fieldType == Integer.class) {
            int fallback = defaultValue instanceof Number ? ((Number) defaultValue).intValue() : 0;
            return readable.getInt(path, fallback);
        }
        if (fieldType == long.class || fieldType == Long.class) {
            long fallback = defaultValue instanceof Number ? ((Number) defaultValue).longValue() : 0L;
            return readable.getLong(path, fallback);
        }
        if (fieldType == double.class || fieldType == Double.class) {
            double fallback = defaultValue instanceof Number ? ((Number) defaultValue).doubleValue() : 0.0D;
            return readable.getDouble(path, fallback);
        }
        if (fieldType == float.class || fieldType == Float.class) {
            float fallback = defaultValue instanceof Number ? ((Number) defaultValue).floatValue() : 0.0F;
            return readable.getFloat(path, fallback);
        }
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            boolean fallback = defaultValue instanceof Boolean && (boolean) defaultValue;
            return readable.getBoolean(path, fallback);
        }
        if (fieldType == UUID.class) {
            return readable.getUUID(path, (UUID) defaultValue);
        }
        if (fieldType == Version.class) {
            return readable.getVersion(path, (Version) defaultValue);
        }
        if (fieldType.isEnum()) {
            return resolveEnum(readable, path, fieldType, defaultValue);
        }
        if (Collection.class.isAssignableFrom(fieldType)) {
            Class<?> genericType = field.collectionElementType();

            if (genericType == null) {
                throw new IllegalArgumentException(
                        "Unsupported collection field without resolvable generic type: " + field.name() +
                                " (path='" + path + "', type=" + fieldType.getName() + ')'
                );
            }

            if (List.class.isAssignableFrom(fieldType)) {
                return readable.getList(path, genericType, List.of());
            }

            if (Set.class.isAssignableFrom(fieldType)) {
                Collection<?> collection = readable.getList(path, genericType, List.of());
                return Set.copyOf(collection);
            }
        }

        Object value = readable.get(path, fieldType);
        if (value != null) {
            return value;
        }

        return readable.getSerializable(path, (Class<Object>) fieldType);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E resolveEnum(
            @NotNull Readable readable,
            @NotNull String path,
            @NotNull Class<?> enumType,
            @Nullable Object defaultValue
    ) {
        Class<E> castedType = (Class<E>) enumType;
        E fallback = castedType.isInstance(defaultValue) ? castedType.cast(defaultValue) : null;
        return readable.getEnum(path, castedType, fallback);
    }

    private static final class BoundType<T> {

        private final Class<T> type;
        private final ConfigSource source;
        private final ConstructorAccessor constructor;
        private final List<BoundField> fields;
        private final List<BoundHook> preHooks;
        private final List<BoundHook> postHooks;

        private BoundType(
                @NotNull Class<T> type,
                @Nullable ConfigSource source,
                @NotNull ConstructorAccessor constructor,
                @NotNull List<BoundField> fields,
                @NotNull List<BoundHook> preHooks,
                @NotNull List<BoundHook> postHooks
        ) {
            this.type = type;
            this.source = source;
            this.constructor = constructor;
            this.fields = fields;
            this.preHooks = preHooks;
            this.postHooks = postHooks;
        }

        @NotNull
        private Class<T> type() {
            return this.type;
        }

        @Nullable
        private ConfigSource source() {
            return this.source;
        }

        @NotNull
        private ConstructorAccessor constructor() {
            return this.constructor;
        }

        @NotNull
        private List<BoundField> fields() {
            return this.fields;
        }

        @NotNull
        private List<BoundHook> preHooks() {
            return this.preHooks;
        }

        @NotNull
        private List<BoundHook> postHooks() {
            return this.postHooks;
        }
    }

    private static final class BoundHook {

        private final MethodAccessor accessor;
        private final Class<?> parameterType;
        private final boolean returnsPostLoadResult;

        @Nullable
        private static BoundHook of(
                @NotNull Method method,
                @NotNull Class<? extends java.lang.annotation.Annotation> annotation
        ) {
            if (!method.isAnnotationPresent(annotation)) {
                return null;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("Hook method must not be static: " + method);
            }

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length > 1) {
                throw new IllegalArgumentException("Hook method must have zero or one parameter: " + method);
            }

            Class<?> returnType = method.getReturnType();
            boolean returnsPostLoadResult = false;
            if (annotation == PostLoad.class) {
                if (returnType == PostLoadResult.class) {
                    returnsPostLoadResult = true;
                } else if (returnType != void.class) {
                    throw new IllegalArgumentException("@PostLoad hook method must return void or PostLoadResult: " + method);
                }
            } else if (returnType != void.class) {
                throw new IllegalArgumentException("Hook method must return void: " + method);
            }

            Class<?> parameterType = parameters.length == 0 ? null : parameters[0];
            return new BoundHook(Accessor.getMethod(method), parameterType, returnsPostLoadResult);
        }

        private BoundHook(
                @NotNull MethodAccessor accessor,
                @Nullable Class<?> parameterType,
                boolean returnsPostLoadResult
        ) {
            this.accessor = accessor;
            this.parameterType = parameterType;
            this.returnsPostLoadResult = returnsPostLoadResult;
        }

        @NotNull
        private PostLoadResult invoke(@NotNull Object instance, @NotNull Readable readable) {
            Object result;

            if (this.parameterType == null) {
                result = this.accessor.invoke(instance);
            } else {
                if (!this.parameterType.isInstance(readable)) {
                    throw new IllegalArgumentException("Hook parameter type is not compatible with readable instance: " + this.accessor.getMethod());
                }
                result = this.accessor.invoke(instance, readable);
            }

            if (this.returnsPostLoadResult && result instanceof PostLoadResult) {
                return (PostLoadResult) result;
            }

            return PostLoadResult.NONE;
        }
    }

    private static final class BoundField {

        private final FieldAccessor accessor;
        private final String name;
        private final Class<?> type;
        private final Class<?> collectionElementType;
        private final ConfigProperty property;

        @Nullable
        private static BoundField of(@NotNull Field field) {
            if (field.isAnnotationPresent(ConfigIgnore.class)) {
                return null;
            }

            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                return null;
            }

            FieldAccessor accessor = Accessor.getField(field);
            return new BoundField(
                    accessor,
                    field.getName(),
                    field.getType(),
                    Reflection.resolveCollectionElementType(field),
                    field.getAnnotation(ConfigProperty.class)
            );
        }

        private BoundField(
                @NotNull FieldAccessor accessor,
                @NotNull String name,
                @NotNull Class<?> type,
                @Nullable Class<?> collectionElementType,
                @Nullable ConfigProperty property
        ) {
            this.accessor = accessor;
            this.name = name;
            this.type = type;
            this.collectionElementType = collectionElementType;
            this.property = property;
        }

        @NotNull
        private String name() {
            return this.name;
        }

        @NotNull
        private Class<?> type() {
            return this.type;
        }

        @Nullable
        private Class<?> collectionElementType() {
            return this.collectionElementType;
        }

        @Nullable
        private ConfigProperty property() {
            return this.property;
        }

        @Nullable
        private Object get(@NotNull Object instance) {
            return this.accessor.get(instance);
        }

        private void set(@NotNull Object instance, @NotNull Object value) {
            try {
                this.accessor.set(instance, value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to set field '" + this.name + "' with value type " + value.getClass()
                        .getName(), ex);
            }
        }
    }

}

