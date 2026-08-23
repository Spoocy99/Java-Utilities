package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Writeable;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.Accessor;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BoundField {

    private final FieldAccessor accessor;

    private final String fieldName;
    private final String propertyKey;
    private final boolean saveDefault;
    private final String[] comments;
    private final String[] inlineComments;
    private final Class<?> type;
    private final Class<?> collectionElementType;
    private final PropertyLoader loader;

    @NotNull
    public static BoundField of(@NotNull ConfigBean<?> bean, @NotNull Field field) {
        FieldAccessor accessor = Accessor.getField(field);

        String fieldName = field.getName();

        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String propertyKey = annotation != null ? annotation.value() : toPropertyName(fieldName);
        String[] comments = annotation != null ? annotation.comments() : new String[0];
        String[] inlineComments = annotation != null ? annotation.inlineComments() : new String[0];
        boolean saveDefault = annotation != null ? annotation.saveDefault() : bean.saveDefaults();

        return new BoundField(
                accessor,
                fieldName,
                propertyKey,
                saveDefault,
                comments,
                inlineComments,
                field.getType(),
                Reflection.resolveCollectionElementType(field)
        );
    }

    private static String toPropertyName(@NotNull String fieldName) {
        StringBuilder builder = new StringBuilder(fieldName.length());
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);

            if (Character.isUpperCase(c)) {
                builder.append('-');
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }

        }

        return builder.toString();
    }

    private BoundField(
            @NotNull FieldAccessor accessor,
            @NotNull String fieldName,
            @NotNull String propertyKey,
            boolean saveDefault,
            @NotNull String[] comments,
            @NotNull String[] inlineComments,
            @NotNull Class<?> type,
            @Nullable Class<?> collectionElementType
    ) {
        this.accessor = accessor;
        this.fieldName = fieldName;
        this.propertyKey = propertyKey;
        this.saveDefault = saveDefault;
        this.comments = comments;
        this.inlineComments = inlineComments;
        this.type = type;
        this.collectionElementType = collectionElementType;
        this.loader = new DefaultPropertyLoader();
    }

    @NotNull
    public String name() {
        return this.fieldName;
    }

    @NotNull
    public String propertyKey() {
        return this.propertyKey;
    }

    public boolean shouldSaveDefault() {
        return this.saveDefault;
    }

    @Nullable
    public String[] comments() {
        return this.comments;
    }

    @Nullable
    public String[] inlineComments() {
        return this.inlineComments;
    }

    @NotNull
    public Class<?> type() {
        return this.type;
    }

    @Nullable
    public Class<?> collectionElementType() {
        return this.collectionElementType;
    }

    @Nullable
    public Object get(@NotNull Object instance) {
        return this.accessor.get(instance);
    }

    public void load(@NotNull Object instance, @NotNull ConfigSection section) {
        Object value = this.loader.load(section, this);
        if (value == null) {
            // wrong data type or no data set
            return;
        }

        set(instance, value);
    }

    public void save(@NotNull Object instance, @NotNull Writeable writable) {
        Object value = this.accessor.get(instance);
        writable.set(this.propertyKey, value);
        writable.setInlineComments(this.propertyKey, this.inlineComments);
        writable.setComments(this.propertyKey, this.comments);
    }

    public boolean saveIfMissing(@NotNull Object instance, @NotNull Writeable writable) {
        if (writable.isSet(this.propertyKey)) {
            return false;
        }

        save(instance, writable);
        return true;
    }

    private void set(@NotNull Object instance, @Nullable Object value) {

        if (value == null && this.type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot assign null to primitive field: '" + this.fieldName + "' (" + this.type.getName() + ") << null");
        }

        try {
            this.accessor.set(instance, value);
        } catch (Exception ex) {
            String valueType = value == null ? "null" : value.getClass().getName();
            throw new IllegalArgumentException("Failed to set field: '" + this.fieldName + "' (" + this.type.getName() + ") << " + valueType, ex);
        }

    }

}
