package dev.spoocy.utils.config.components;

import dev.spoocy.utils.config.serializer.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigNode {

    @NotNull
    private Tag tag;

    @Nullable
    private Class<?> type;

    @Nullable
    private Object data;

    @NotNull
    private List<String> comments;

    @NotNull
    private List<String> inlineComments;

    public ConfigNode(@Nullable Object data) {
        this(data, null, null);
    }

    public ConfigNode(
            @Nullable Object data,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        this.comments = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
        this.inlineComments = inlineComments == null ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);

        setData(data);
    }

    @NotNull
    public Tag getTag() {
        return this.tag;
    }

    @Nullable
    public Object getData() {
        return this.data;
    }

    @Nullable
    public Class<?> getType() {
        return this.type;
    }

    public void setData(@Nullable Object data) {
        this.data = data;
        this.type = data == null ? null : data.getClass();
        this.tag = resolveTag(data);
    }

    @NotNull
    private static Tag resolveTag(@Nullable Object data) {
        if (data == null) {
            return Tag.NULL;
        }

        if (data instanceof MemorySection) {
            return Tag.SECTION;
        }

        if (data instanceof java.util.Map) {
            return Tag.MAP;
        }

        if (data instanceof java.util.Collection || data.getClass().isArray()) {
            return Tag.SEQ;
        }

        if (data instanceof Boolean) {
            return Tag.BOOL;
        }

        if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof BigInteger) {
            return Tag.INT;
        }

        if (data instanceof Float || data instanceof Double || data instanceof BigDecimal) {
            return Tag.FLOAT;
        }

        if (data instanceof CharSequence || data instanceof Character) {
            return Tag.STR;
        }

        return new Tag(data.getClass());
    }

    @NotNull
    public List<String> getComments() {
        return this.comments;
    }

    public void setComments(@Nullable List<String> comments) {
        this.comments = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    @NotNull
    public List<String> getInlineComments() {
        return this.inlineComments;
    }

    public void setInlineComments(@Nullable List<String> inlineComments) {
        this.inlineComments = inlineComments == null ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);
    }
}
