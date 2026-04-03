package dev.spoocy.utils.config.serializer;

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

    public static final Set<Tag> STANDARD_TAGS = new HashSet<>(9);
    private static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<>();

    public static final String PREFIX = "tag:dev.spoocy.utils.config:";
    public static final Tag SECTION = forName("section");
    public static final Tag NULL = forName("null");
    public static final Tag INT = forName("int");
    public static final Tag FLOAT = forName("float");
    public static final Tag TIMESTAMP = forName("timestamp");
    public static final Tag BOOL = forName("bool");
    public static final Tag STR = forName("str");
    public static final Tag SEQ = forName("seq");
    public static final Tag MAP = forName("map");

    private static Tag forName(@NotNull String tagName) {
        Tag tag = new Tag(PREFIX + tagName);
        STANDARD_TAGS.add(tag);
        return tag;
    }

    static {
        COMPATIBILITY_MAP.put(
                FLOAT,
                Set.of(
                        Double.class,
                        Float.class,
                        BigDecimal.class
                )
        );

        COMPATIBILITY_MAP.put(INT, Set.of(
                Integer.class,
                Long.class,
                BigInteger.class
        ));

        Set<Class<?>> timestampSet = new HashSet<>();
        timestampSet.add(Date.class);

        try {
            timestampSet.add(Class.forName("java.sql.Date"));
            timestampSet.add(Class.forName("java.sql.Timestamp"));
        } catch (ClassNotFoundException ignored) { }

        COMPATIBILITY_MAP.put(TIMESTAMP, timestampSet);
    }

    private final String value;
    private final boolean secondary;

    public Tag(@NotNull String tag) {
        this.value = Args.notNullOrEmpty(tag, "tag");

        if (tag.trim().length() != tag.length()) {
            throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
        }

        this.secondary = !tag.startsWith(PREFIX);
    }

    public Tag(@NotNull Class<?> clazz) {
        Args.notNull(clazz, "class");
        this.value = PREFIX + clazz.getName();
        this.secondary = false;
    }

    public boolean isStandard() {
        return STANDARD_TAGS.contains(this);
    }

    public boolean isSecondary() {
        return this.secondary;
    }

    public String getValue() {
        return this.value;
    }

    public boolean startsWith(String prefix) {
        return this.value.startsWith(prefix);
    }

    public String getClassName() {
        if (secondary) {
            throw new IllegalStateException("Cannot get class name from a secondary tag");
        }

        String name = value.substring(Tag.PREFIX.length());
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
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

    /**
     * Java has more than 1 class compatible with a language-independent tag (!!int, !!float,
     * !!timestamp etc)
     *
     * @param clazz - Class to check compatibility
     *
     * @return true when the Class can be represented by this language-independent tag
     */
    public boolean isCompatible(@NotNull Class<?> clazz) {
        Set<Class<?>> set = COMPATIBILITY_MAP.get(this);

        if (set != null) {
            return set.contains(clazz);
        } else {
            return false;
        }
    }

    /**
     * Check whether this tag matches the global tag for the Class
     *
     * @param clazz - Class to check
     *
     * @return true when this tag can be used as a global tag for the Class during serialisation
     */
    public boolean matches(@NotNull Class<?> clazz) {
        return value.equals(Tag.PREFIX + clazz.getName());
    }

    /**
     * Check if the that is global and not standard to provide it to TagInspector for verification.
     *
     * @return true when the tag must be verified to avoid remote code invocation
     */
    public boolean isCustomGlobal() {
        return !secondary && !STANDARD_TAGS.contains(this);
    }
}
