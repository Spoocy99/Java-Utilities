package dev.spoocy.utils.config.nodes;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class Node {

    public static final Class<?> NULL_TYPE = void.class;

    @NotNull
    private Tag tag;

    @NotNull
    protected List<String> comments;

    @NotNull
    protected List<String> inlineComments;

    public Node(@NotNull Tag tag, @Nullable List<String> comments, @Nullable List<String> inlineComments) {
        setTag(tag);
        setComments(comments);
        setInlineComments(inlineComments);
    }

    @NotNull
    public Tag getTag() {
        return tag;
    }

    @NotNull
    public List<String> getComments() {
        return comments;
    }

    @NotNull
    public List<String> getInlineComments() {
        return inlineComments;
    }

    public void setTag(@NotNull Tag tag) {
        this.tag = Args.notNull(tag, "tag");
    }

    public void setComments(@Nullable List<String> comments) {
        this.comments = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    public void setInlineComments(@Nullable List<String> inlineComments) {
        this.inlineComments = inlineComments == null ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);
    }

    @NotNull
    public abstract NodeType getNodeType();

    public String displayForm() {
        return NodeTree.displayForm(0, this);
    }

    public String displayForm(int indent) {
        return NodeTree.displayForm(indent, this);
    }

}
