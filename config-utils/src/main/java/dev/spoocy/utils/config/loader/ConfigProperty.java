package dev.spoocy.utils.config.loader;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares how a field is mapped to a config path.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

    /**
     * Path relative to the source section. If empty, the field name is used.
     */
    @NotNull
    String value() default "";

    /**
     * Overrides source-level saveDefaults behavior for this field.
     */
    boolean saveDefault() default true;

    /**
     * Block comments written above the resolved config path.
     */
    @NotNull
    String[] comments() default {};

    /**
     * Inline comments written next to the resolved config path.
     */
    @NotNull
    String[] inlineComments() default {};

}

