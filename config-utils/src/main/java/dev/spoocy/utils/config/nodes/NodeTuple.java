package dev.spoocy.utils.config.nodes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class NodeTuple {

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NodeTuple of(@NotNull Node keyNode, @NotNull Node valueNode) {
        return new NodeTuple(keyNode, valueNode);
    }

    @NotNull
    private final Node keyNode;

    @NotNull
    private final Node valueNode;

    private NodeTuple(@NotNull Node keyNode, @NotNull Node valueNode) {
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    @NotNull
    public Node getKeyNode() {
        return this.keyNode;
    }

    @NotNull
    public Node getValueNode() {
        return this.valueNode;
    }

    @Override
    public String toString() {
        return "<Tuple [" + this.keyNode + ", " + this.valueNode + "]>";
    }

    public String displayForm() {
        return displayForm(0);
    }

    public String displayForm(int indent) {
        String spaces = " ".repeat(Math.max(0, indent));
        return spaces + "<Tuple [\n" +
                NodeTree.displayForm(indent + 2, this.keyNode) + ",\n" +
                NodeTree.displayForm(indent + 2, this.valueNode) + "\n" +
                spaces + "]>";
    }
}