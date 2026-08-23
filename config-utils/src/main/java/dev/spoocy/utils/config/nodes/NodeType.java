package dev.spoocy.utils.config.nodes;

/**
 * Represents a scalar node type in a configuration structure.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public enum NodeType {

    /**
     * A scalar node represents a single value.
     * <p>
     * {@link ScalarNode}
     */
    SCALAR,

    /**
     * A sequence node corresponds to an ordered collection of elements.
     * <p>
     * {@link SequenceNode}
     */
    SEQUENCE,

    /**
     * A tree node corresponds to a collection of key-value pairs.
     * <p>
     * {@link NodeTree}
     */
    TREE
}
