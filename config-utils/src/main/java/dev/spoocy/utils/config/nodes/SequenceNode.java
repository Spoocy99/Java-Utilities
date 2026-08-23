package dev.spoocy.utils.config.nodes;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SequenceNode extends CollectionNode<Node> {

    private List<Node> values;

    public SequenceNode(
            @NotNull Tag tag,
            @NotNull List<Node> values,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        super(tag, comments, inlineComments);
        setValue(values);
    }

    public void setValue(@NotNull List<Node> values) {
        this.values = Args.notNull(values, "values");
    }

    public int size() {
        return this.values.size();
    }

    @Override
    public List<Node> getValue() {
        return this.values;
    }

    @Override
    public @NotNull Iterator<Node> iterator() {
        return this.values.iterator();
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.SEQUENCE;
    }

    @Override
    public String toString() {
        return "<Sequence " + getTag() + " [" + formatSequence() + "]>";
    }

    private String formatSequence() {
        return this.values.toString();
    }

}
