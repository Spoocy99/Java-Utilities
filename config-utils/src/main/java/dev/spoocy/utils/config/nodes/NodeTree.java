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

public class NodeTree extends CollectionNode<NodeTuple> {

    private List<NodeTuple> values;

    public NodeTree(
            @NotNull Tag tag,
            @NotNull List<NodeTuple> values,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        super(tag, comments, inlineComments);
        setValue(values);
    }

    public void setValue(@NotNull List<NodeTuple> values) {
        this.values = Args.notNull(values, "values");
    }

    public int size() {
        return this.values.size();
    }

    @Override
    public List<NodeTuple> getValue() {
        return this.values;
    }

    @NotNull
    @Override
    public Iterator<NodeTuple> iterator() {
        return this.values.iterator();
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.TREE;
    }

    @Override
    public String toString() {
        return "<Tree " + getTag() + " [" + formatSequence() + "]>";
    }

    private String formatSequence() {
        return this.values.toString();
    }

    public String displayForm() {
        return displayForm(0, this);
    }

    public String displayForm(int indent) {
        return displayForm(indent, this);
    }

    public static @NotNull String displayForm(@NotNull Node node) {
        return displayForm(0, node);
    }

    public static @NotNull String displayForm(int indent, @NotNull Node node) {
        Args.notNull(node, "node");
        String spaces = " ".repeat(Math.max(0, indent));

        if (node instanceof ScalarNode) {
            return spaces + node;
        }

        Tag tag = node.getTag();

        if (node instanceof SequenceNode) {
            SequenceNode sequenceNode = (SequenceNode) node;
            List<Node> values = sequenceNode.getValue();
            if (values.isEmpty()) {
                return spaces + "<Sequence " + tag + " []>";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(spaces).append("<Sequence ").append(tag).append(" [\n");
            for (int i = 0; i < values.size(); i++) {
                Node child = values.get(i);
                builder.append(displayForm(indent + 2, child));
                if (i < values.size() - 1) {
                    builder.append(",");
                }
                builder.append("\n");
            }
            builder.append(spaces).append("]>");
            return builder.toString();
        }

        if (node instanceof NodeTree) {
            NodeTree treeNode = (NodeTree) node;
            List<NodeTuple> values = treeNode.getValue();
            if (values.isEmpty()) {
                return spaces + "<Tree " + tag + " []>";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(spaces).append("<Tree ").append(tag).append(" [\n");
            for (int i = 0; i < values.size(); i++) {
                NodeTuple tuple = values.get(i);
                builder.append(spaces).append("  <Tuple [\n");
                builder.append(displayForm(indent + 4, tuple.getKeyNode())).append(",\n");
                builder.append(displayForm(indent + 4, tuple.getValueNode())).append("\n");
                builder.append(spaces).append("  ]>");
                if (i < values.size() - 1) {
                    builder.append(",");
                }
                builder.append("\n");
            }
            builder.append(spaces).append("]>");
            return builder.toString();
        }

        return spaces + node;
    }

}
