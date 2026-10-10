package dev.spoocy.utils.config.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be ignored by the {@link ConfigBeanLoaderImpl}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
}

