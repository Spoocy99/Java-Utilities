package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.common.misc.NumberConversion;
import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.nodes.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SafeConstructor extends BaseConstructor {

    public SafeConstructor() {
        super();

        construct(Tag.STR, new StringConstructor());
        construct(Tag.BOOL, new BooleanConstructor());
        construct(Tag.INT, new IntConstructor());
        construct(Tag.FLOAT, new FloatConstructor());
        construct(Tag.SET, new SetConstructor());
        construct(Tag.SEQ, new SequenceConstructor());
        construct(Tag.MAP, new MapConstructor());
    }

    protected class StringConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof ScalarNode)) {
                throw new IllegalArgumentException("Tried to construct non-scalar data.");
            }
            return constructScalarString((ScalarNode) node);
        }
    }

    protected class BooleanConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof ScalarNode)) {
                throw new IllegalArgumentException("Tried to construct non-scalar data.");
            }

            ScalarNode scalarNode = (ScalarNode) node;
            Object data = scalarNode.getData();

            if(data == null) {
                return false;
            }

            String value = data.toString().toLowerCase();

            if (value.equals("true") || value.equals("yes") || value.equals("on")) {
                return true;

            } else if (value.equals("false") || value.equals("no") || value.equals("off")) {
                return false;

            } else {
                throw new IllegalArgumentException("Invalid boolean value: " + scalarNode.getData());
            }
        }
    }

    protected class IntConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {

            if (!(node instanceof ScalarNode)) {
                throw new IllegalArgumentException("Tried to construct non-scalar data.");
            }

            ScalarNode scalarNode = (ScalarNode) node;
            Object data = scalarNode.getData();

            if (data instanceof Number) {
                return ((Number) data).longValue();
            }

            return NumberConversion.toLong(data);
        }

    }

    protected static class FloatConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof ScalarNode)) {
                throw new IllegalArgumentException("Tried to construct non-scalar data.");
            }

            ScalarNode scalarNode = (ScalarNode) node;
            Object data = scalarNode.getData();

            if (data instanceof Number) {
                return ((Number) data).doubleValue();
            }

            return NumberConversion.toDouble(data);
        }
    }


    protected class SetConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof SequenceNode)) {
                throw new IllegalArgumentException("Tried to construct non-sequence data.");
            }

            SequenceNode sequenceNode = (SequenceNode) node;

            Set<Object> set = createSet(sequenceNode.size());

            for (Node item : sequenceNode) {
                set.add(constructObject(item));
            }

            return set;
        }
    }

    protected class SequenceConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof SequenceNode)) {
                throw new IllegalArgumentException("Tried to construct non-sequence data.");
            }

            SequenceNode sequenceNode = (SequenceNode) node;

            List<Object> list = createList(sequenceNode.size());

            for (Node item : sequenceNode) {
                list.add(constructObject(item));
            }

            return list;
        }
    }

    protected class MapConstructor implements Construct {

        @Override
        public @Nullable Object construct(@Nullable Node node) {
            if (!(node instanceof NodeTree)) {
                throw new IllegalArgumentException("Tried to construct non-sequence data.");
            }

            NodeTree tree = (NodeTree) node;
            Map<Object, Object> map = createMap(tree.size());

            for (NodeTuple tuple : tree) {
                Object key = constructObject(tuple.getKeyNode());
                Object value = constructObject(tuple.getValueNode());
                map.put(key, value);
            }

            return map;
        }
    }

}
