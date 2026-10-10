package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Writeable;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.Accessor;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BoundField {

    private final boolean isStatic;
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
        boolean isStatic = Modifier.isStatic(field.getModifiers());

        String fieldName = field.getName();

        Property annotation = field.getAnnotation(Property.class);
        String propertyKey = annotation != null ? annotation.value() : toPropertyName(fieldName);
        String[] comments = annotation != null ? annotation.comments() : new String[0];
        String[] inlineComments = annotation != null ? annotation.inlineComments() : new String[0];
        boolean saveDefault = annotation != null ? annotation.saveDefault() : bean.saveDefaults();

        return new BoundField(
                isStatic,
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

    @NotNull
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
            boolean isStatic,
            @NotNull FieldAccessor accessor,
            @NotNull String fieldName,
            @NotNull String propertyKey,
            boolean saveDefault,
            @NotNull String[] comments,
            @NotNull String[] inlineComments,
            @NotNull Class<?> type,
            @Nullable Class<?> collectionElementType
    ) {
        this.isStatic = isStatic;
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

    public boolean isStatic() {
        return this.isStatic;
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

    public void load(@Nullable Object instance, @NotNull ConfigSection section) {
        if(instance == null && !this.isStatic) {
            throw new IllegalArgumentException("Instance not provided for non-static field: " + this.fieldName);
        }

        Object value = this.loader.load(section, this);
        if (value == null) {
            // wrong data type or no data set
            return;
        }

        set(instance, value);
    }

    public void save(@Nullable Object instance, @NotNull Writeable writable) {
        if(instance == null && !this.isStatic) {
            throw new IllegalArgumentException("Instance not provided for non-static field: " + this.fieldName);
        }

        Object value = this.accessor.get(instance);
        writable.set(this.propertyKey, value);
        writable.setInlineComments(this.propertyKey, this.inlineComments);
        writable.setComments(this.propertyKey, this.comments);
    }

    public boolean saveIfMissing(@Nullable Object instance, @NotNull Writeable writable) {
        if(instance == null && !this.isStatic) {
            throw new IllegalArgumentException("Instance not provided for non-static field: " + this.fieldName);
        }

        if (writable.isSet(this.propertyKey)) {
            return false;
        }

        save(instance, writable);
        return true;
    }

    private void set(@Nullable Object instance, @Nullable Object value) {

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
