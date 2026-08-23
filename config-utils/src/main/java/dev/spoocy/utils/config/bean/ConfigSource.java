package dev.spoocy.utils.config.bean;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares where an annotated config class should be loaded from.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigSource {

    /**
     * Resource location (e.g. classpath:config.yml, file:C:/config.yml).
     */
    @NotNull
    String value() default "";

    /**
     * Optional base section used for all bound properties.
     */
    @NotNull
    String section() default "";

    /**
     * Persist missing values from field defaults back to the document when
     * using {@link LoadStrategy#SAVE_DEFAULTS} or {@link LoadStrategy#SAVE_DEFAULTS_AND_RESOURCE}.
     */
    boolean saveDefaults() default false;

    /**
     * Allows loading from a non-existing resource by starting with an empty document.
     */
    boolean allowMissingResource() default true;

    /**
     * Header comments written at the start of the document.
     */
    @NotNull
    String[] headerComments() default {};

    /**
     * Footer comments written at the end of the document.
     */
    @NotNull
    String[] footerComments() default {};

}

