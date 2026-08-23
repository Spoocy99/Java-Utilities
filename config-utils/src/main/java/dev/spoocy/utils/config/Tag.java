package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class Tag {

    public static final Set<Tag> STANDARD_TAGS = new HashSet<>(8);
    private static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<>(6);

    public static final String PREFIX = "tag:type:";

    public static final Tag NULL = forStandard("null",
            Void.class, void.class
    );
    public static final Tag STR = forStandard("str",
            String.class
    );
    public static final Tag BOOL = forStandard("bool",
            boolean.class, Boolean.class
    );
    public static final Tag INT = forStandard("int",
            int.class, Integer.class,
            long.class, Long.class,
            BigInteger.class
    );
    public static final Tag FLOAT = forStandard("float",
            double.class, Double.class,
            float.class, Float.class,
            BigDecimal.class
    );
    public static final Tag SET = forStandard("set");
    public static final Tag SEQ = forStandard("seq");
    public static final Tag MAP = forStandard("map");

    @NotNull
    private static Tag forStandard(@NotNull String tagName, @NotNull Class<?>... compatibleTypes) {

        Tag tag = new Tag(PREFIX + tagName);
        STANDARD_TAGS.add(tag);

        if (compatibleTypes.length != 0) {
            COMPATIBILITY_MAP.put(tag, new HashSet<>(Arrays.asList(compatibleTypes)));
        }

        return tag;
    }

    private final String value;
    private final boolean custom;

    public Tag(@NotNull String tag) {
        this.value = Args.notNullOrEmpty(tag, "tag");

        if (tag.trim().length() != tag.length()) {
            throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
        }

        this.custom = !this.value.startsWith(PREFIX);
    }

    public Tag(@NotNull Class<?> clazz) {
        Args.notNull(clazz, "class");
        this.value = PREFIX + clazz.getName();
        this.custom = false;
    }

    public boolean isStandard() {
        return STANDARD_TAGS.contains(this);
    }

    public String getValue() {
        return this.value;
    }

    public boolean isCustom() {
        return this.custom;
    }

    public boolean startsWith(@NotNull String prefix) {
        return this.value.startsWith(prefix);
    }

    public String getClassName() {
        if (this.custom) {
            throw new IllegalStateException("Cannot get class name from a custom tag");
        }

        String name = value.substring(Tag.PREFIX.length());
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }

    public boolean isCompatible(@NotNull Class<?> clazz) {
        Set<Class<?>> set = COMPATIBILITY_MAP.get(this);

        if (set != null) {
            return set.contains(clazz);
        }

        if (!this.custom) {
            String className = getClassName();
            return className.equals(clazz.getName());
        }

        return false;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            return this.value.equals(other.value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public static Tag getDefaultTag(@NotNull Class<?> clazz) {
        if (NULL.isCompatible(clazz)) {
            return NULL;
        }

        if (INT.isCompatible(clazz)) {
            return INT;
        }

        if (FLOAT.isCompatible(clazz)) {
            return FLOAT;
        }

        if (BOOL.isCompatible(clazz)) {
            return BOOL;
        }

        if (STR.isCompatible(clazz)) {
            return STR;
        }

        if(Set.class.isAssignableFrom(clazz)) {
            return Tag.SET;
        }

        if(Iterable.class.isAssignableFrom(clazz) || clazz.isArray()) {
            return Tag.SEQ;
        }

        if(Map.class.isAssignableFrom(clazz)) {
            return Tag.MAP;
        }

        return new Tag(clazz);
    }

}
