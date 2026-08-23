package dev.spoocy.utils.config.nodes;

import dev.spoocy.utils.config.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ScalarNode extends Node {

    public static ScalarNode nullValue() {
        return new ScalarNode(null, Tag.NULL, null, null);
    }

    private Object data;

    public ScalarNode(
            @Nullable Object data,
            @NotNull Tag tag,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        super(tag, comments, inlineComments);
        this.data = data;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.SCALAR;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    @Nullable
    public String getValue() {
        if (data == null) {
            return null;
        }
        return data.toString();
    }

    public void setData(@Nullable Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "<Scalar " + getTag() + " (" + getData() + ")>";
    }
}
