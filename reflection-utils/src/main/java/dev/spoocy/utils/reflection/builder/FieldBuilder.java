package dev.spoocy.utils.reflection.builder;

import dev.spoocy.utils.reflection.matcher.ClassMatcher;
import dev.spoocy.utils.reflection.matcher.FieldMatcher;
import dev.spoocy.utils.reflection.matcher.IMatcher;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public class FieldBuilder {

    @Contract(" -> new")
    public static @NotNull FieldBuilder create() {
        return new FieldBuilder();
    }

    @Contract("_ -> new")
    public static @NotNull FieldBuilder copy(@NotNull FieldBuilder builder) {
        return new FieldBuilder(builder);
    }

    private Pattern name;
    private int requiredModifiers;
    private int excludedModifiers;
    private IMatcher<Class<?>> type = ClassMatcher.MATCH_ALL;

    private final List<GenericType> genericTypes;
    private final List<Class<? extends Annotation>> annotations;
    private final List<Class<? extends Annotation>> excludedAnnotations;

    private FieldBuilder() {
        this.annotations = new ArrayList<>();
        this.excludedAnnotations = new ArrayList<>();
        this.genericTypes = new ArrayList<>();
    }

    public FieldBuilder(@NotNull FieldBuilder builder) {
        this.name = builder.name;
        this.requiredModifiers = builder.requiredModifiers;
        this.excludedModifiers = builder.excludedModifiers;
        this.type = builder.type;
        this.annotations = new ArrayList<>(builder.annotations);
        this.excludedAnnotations = new ArrayList<>(builder.excludedAnnotations);
        this.genericTypes = new ArrayList<>(builder.genericTypes);
    }

    /**
     * Sets the field name to match any name.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder anyName() {
        this.name = null;
        return this;
    }

    /**
     * Sets the name pattern of the field to match.
     * The name can be a regular expression.
     *
     * @param name The name pattern to match.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder name(@NotNull String name) {
        this.name = Pattern.compile(name);
        return this;
    }

    /**
     * Sets the name pattern of the field to match.
     * The name can be a regular expression.
     *
     * @param name The name pattern of the field to match.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder name(@NotNull Pattern name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the exact name of the field to match.
     *
     * @param name The exact name of the field to match.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder nameExact(@NotNull String name) {
        return this.name(Pattern.quote(name));
    }

    /**
     * Requires the given modifier for the field.
     *
     * @param modifier The modifier to require.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requireModifier(int modifier) {
        this.requiredModifiers |= modifier;
        return this;
    }

    /**
     * Requires the field to be static.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requireStatic() {
        return requireModifier(Modifier.STATIC);
    }

    /**
     * Requires the field to be public.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requirePublic() {
        return requireModifier(Modifier.PUBLIC);
    }

    /**
     * Excludes the given modifier from the field.
     *
     * @param modifier The modifier to exclude.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder excludeModifiers(int modifier) {
        this.excludedModifiers |= modifier;
        return this;
    }

    /**
     * Requires the given annotation to be present on the field.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requireAnnotation(@NotNull Class<? extends Annotation> annotations) {
        this.annotations.add(annotations);
        return this;
    }

    /**
     * Excludes the given annotation from being present on the field.
     *
     * @param annotation The annotation to exclude.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder excludeAnnotation(@NotNull Class<? extends Annotation> annotation) {
        this.excludedAnnotations.add(annotation);
        return this;
    }

    /**
     * Requires the field to have generic types matching the given types.
     *
     * @param types The generic types to require.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requireGenericTypes(@NotNull Class<?>... types) {
        for (int i = 0; i < types.length; i++) {
            this.genericTypes.add(new GenericType(new ClassMatcher(types[i], ClassMatcher.MatchType.MATCH), i));
        }
        return this;
    }

    /**
     * Requires the field to have generic types exactly matching the given types.
     *
     * @param types The generic types to require.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder requireGenericTypesExact(@NotNull Class<?>... types) {
        for (int i = 0; i < types.length; i++) {
            this.genericTypes.add(new GenericType(new ClassMatcher(types[i], ClassMatcher.MatchType.MATCH_EXACT), i));
        }
        return this;
    }

    /**
     * Sets the required type for the field.
     *
     * @param type The type.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder type(@NotNull Class<?> type) {
        this.type = new ClassMatcher(type, ClassMatcher.MatchType.MATCH);
        return this;
    }

    /**
     * Sets the required type for the field exactly.
     *
     * @param type The type.
     *
     * @return The current {@link FieldBuilder} instance.
     */
    public FieldBuilder typeExact(@NotNull Class<?> type) {
        this.type = new ClassMatcher(type, ClassMatcher.MatchType.MATCH_EXACT);
        return this;
    }

    /**
     * Builds the {@link IMatcher<Field>} instance.
     *
     * @return The built matcher instance.
     */
    public IMatcher<Field> build() {
        return new FieldMatcher(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FieldBuilder)) return false;

        FieldBuilder that = (FieldBuilder) obj;

        return requiredModifiers == that.requiredModifiers
                && excludedModifiers == that.excludedModifiers
                && equalsPattern(name, that.name)
                && Objects.equals(annotations, that.annotations)
                && Objects.equals(excludedAnnotations, that.excludedAnnotations)
                && Objects.equals(genericTypes, that.genericTypes)
                && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requiredModifiers, excludedModifiers, annotations, excludedAnnotations, genericTypes, type);
    }

    @Override
    public String toString() {
        return "FieldBuilder{" +
                "annotations=" + annotations +
                ", name=" + name +
                ", requiredModifiers=" + requiredModifiers +
                ", excludedModifiers=" + excludedModifiers +
                ", type=" + type +
                ", excludedAnnotations=" + excludedAnnotations +
                ", genericTypes=" + genericTypes +
                '}';
    }

    private static boolean equalsPattern(@Nullable Pattern a, @Nullable Pattern b) {
        if (a == null) return b == null;
        if (b == null) return false;
        if (a == b) return true;

        return a.pattern().equals(b.pattern());
    }

    @Getter
    public static class GenericType {
        private final IMatcher<Class<?>> type;
        private final int index;

        public GenericType(@NotNull IMatcher<Class<?>> type, int index) {
            this.type = type;
            this.index = index;
        }
    }

}
