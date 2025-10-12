package dev.spoocy.utils.reflection.builder;

import dev.spoocy.utils.reflection.matcher.*;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public class MethodBuilder {

    @Contract(" -> new")
    public static MethodBuilder create() {
        return new MethodBuilder();
    }

    @Contract("_ -> new")
    public static MethodBuilder copy(@NotNull MethodBuilder data) {
        return new MethodBuilder(data);
    }

    private Pattern name;
    private int requiredModifiers;
    private int excludedModifiers;
    private IMatcher<Class<?>> returnType = ClassMatcher.MATCH_ALL;

    private int parameterCount;
    private final List<ParameterMatcher> parameters;
    private final List<Class<? extends Annotation>> annotations;
    private final List<Class<? extends Annotation>> excludedAnnotations;

    private MethodBuilder() {
        this.parameters = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.excludedAnnotations = new ArrayList<>();
    }

    private MethodBuilder(@NotNull MethodBuilder builder) {
        this.name = builder.name;
        this.requiredModifiers = builder.requiredModifiers;
        this.excludedModifiers = builder.excludedModifiers;
        this.returnType = builder.returnType;
        this.parameterCount = builder.parameterCount;
        this.parameters = new ArrayList<>(builder.parameters);
        this.annotations = new ArrayList<>(builder.annotations);
        this.excludedAnnotations = new ArrayList<>(builder.excludedAnnotations);
    }

    /**
     * Sets the method name to match any name.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder anyName() {
        this.name = null;
        return this;
    }

    /**
     * Sets the name pattern of the method to match.
     * The name can be a regular expression.
     *
     * @param name The name pattern to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder name(@NotNull String name) {
        this.name = Pattern.compile(name);
        return this;
    }

    /**
     * Sets the name pattern of the method to match.
     * The name can be a regular expression.
     *
     * @param name The name pattern to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder name(@NotNull Pattern name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the exact name of the method to match.
     *
     * @param name The exact name to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder nameExact(String name) {
        return this.name(Pattern.quote(name));
    }

    /**
     * Requires the given modifier for the method.
     *
     * @param modifier The modifier to require.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder requireModifier(int modifier) {
        this.requiredModifiers |= modifier;
        return this;
    }

    /**
     * Requires the method to be static.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder requireStatic() {
        return requireModifier(Modifier.STATIC);
    }

    /**
     * Requires the method to be public.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder requirePublic() {
        return requireModifier(Modifier.PUBLIC);
    }

    /**
     * Excludes the given modifier from the method.
     *
     * @param modifier The modifier to exclude.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder excludeModifiers(int modifier) {
        this.excludedModifiers |= modifier;
        return this;
    }

    /**
     * Requires the given annotation to be present on the method.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder requireAnnotation(@NotNull Class<? extends Annotation> annotations) {
        this.annotations.add(annotations);
        return this;
    }

    /**
     * Excludes the given annotation from being present on the method.
     *
     * @param annotation The annotation to exclude.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder excludeAnnotation(@NotNull Class<? extends Annotation> annotation) {
        this.excludedAnnotations.add(annotation);
        return this;
    }

    /**
     * Requires the method to have the given return type.
     *
     * @param type The return type to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder returnType(@NotNull Class<?> type) {
        this.returnType = new ClassMatcher(type, ClassMatcher.MatchType.MATCH);
        return this;
    }

    /**
     * Requires the method to have the exact given return type.
     *
     * @param type The exact return type to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder returnTypeExact(@NotNull Class<?> type) {
        this.returnType = new ClassMatcher(type, ClassMatcher.MatchType.MATCH_EXACT);
        return this;
    }

    /**
     * Requires the method to have a void return type.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder returnTypeVoid() {
        return returnTypeExact(Void.TYPE);
    }

    /**
     * Requires the method to have the given parameter count.
     *
     * @param parameterCount The parameter count to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder parameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
        return this;
    }

    /**
     * Requires the method to have a parameter of the given type at the given index.
     *
     * @param index The index of the parameter to match.
     * @param type  The type of the parameter to match.
     *
     * @return The current {@link MethodBuilder} instance.
     */
    public MethodBuilder parameterType(int index, Class<?> type) {
        parameters.add(
                new ParameterMatcher(new ClassMatcher(type, ClassMatcher.MatchType.MATCH_EXACT), index)
        );
        return this;
    }

    /**
     * Builds the {@link IMatcher<Field>} instance.
     *
     * @return The built matcher instance.
     */
    public IMatcher<Method> build() {
        return new MethodMatcher(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FieldBuilder)) return false;

        MethodBuilder other = (MethodBuilder) obj;

        return excludedModifiers == other.getExcludedModifiers()
                && requiredModifiers == other.getRequiredModifiers()
                && equalsPattern(name, other.getName())
                && parameterCount == other.getParameterCount()
                && parameters.equals(other.getParameters())
                && Objects.equals(returnType, other.getReturnType())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.requiredModifiers,
                this.excludedModifiers,
                this.parameterCount,
                this.parameters,
                this.returnType
        );
    }

    @Override
    public String toString() {
        return "MethodData{" +
                "bannedModifiers=" + excludedModifiers +
                ", name=" + name +
                ", requiredModifiers=" + requiredModifiers +
                ", parameterCount=" + parameterCount +
                ", paramMatchers=" + parameters +
                ", returnType=" + returnType +
                '}';
    }

    private static boolean equalsPattern(@Nullable Pattern a, @Nullable Pattern b) {
        if (a == null) return b == null;
        if (b == null) return false;
        if (a == b) return true;

        return a.pattern().equals(b.pattern());
    }

}
