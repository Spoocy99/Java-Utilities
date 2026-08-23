package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.nodes.Node;
import dev.spoocy.utils.config.nodes.NodeTree;
import dev.spoocy.utils.config.nodes.NodeTuple;
import dev.spoocy.utils.config.nodes.NodeType;
import dev.spoocy.utils.config.nodes.ScalarNode;
import dev.spoocy.utils.config.nodes.SequenceNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class RepresenterTest {

    private final CustomRepresenter representer = new CustomRepresenter();

    @Nested
    class Constructors {

        @Test
        public void testSafeRepresenterConstructorRegistersDefaults() {
            SafeRepresenter safeRepresenter = new SafeRepresenter();

            assertNotNull(safeRepresenter.getRepresent(Node.NULL_TYPE));
            assertNotNull(safeRepresenter.getRepresent(String.class));
            assertNotNull(safeRepresenter.getRepresent(Boolean.class));
            assertNotNull(safeRepresenter.getRepresent(Map.class));
            assertNotNull(safeRepresenter.getRepresent(List.class));
            assertNotNull(safeRepresenter.getRepresent(LinkedHashMap.class));
            assertNotNull(safeRepresenter.getRepresent(ArrayList.class));
        }

        @Test
        public void testCustomRepresenterConstructorKeepsDefaultsAndRegistersCustomType() {
            CustomRepresenter customRepresenter = new CustomRepresenter();

            assertNotNull(customRepresenter.getRepresent(Node.NULL_TYPE));
            assertNotNull(customRepresenter.getRepresent(Map.class));
            assertNotNull(customRepresenter.getRepresent(List.class));
            assertNotNull(customRepresenter.getRepresent(DirectObject.class));
        }

    }

    @Nested
    class Primitives {

        @Test
        public void testNull() {
            Node node = representer.representObject(null);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertNull(scalarNode.getData());
            assertEquals(Tag.NULL, scalarNode.getTag());
        }

        @Test
        public void testString() {
            String value = "Hello, World!";
            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals(value, scalarNode.getData());
            assertEquals(Tag.STR, scalarNode.getTag());
        }

        @Test
        public void testBoolean() {
            Boolean value = true;
            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals(value, scalarNode.getData());
            assertEquals(Tag.BOOL, scalarNode.getTag());
        }

        @Test
        public void testInteger() {
            Integer value = 42;
            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals(value, scalarNode.getData());
            assertEquals(Tag.INT, scalarNode.getTag());
        }

        @Test
        public void testFloatingPoint() {
            Double value = 3.14d;
            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals(value, scalarNode.getData());
            assertEquals(Tag.FLOAT, scalarNode.getTag());
        }

    }

    @Nested
    class Collections {

        @Test
        public void testSetSerialization() {
            Set<Object> value = new LinkedHashSet<>();
            value.add("one");
            value.add(2);
            value.add(true);

            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SEQUENCE, node.getNodeType());
            assertInstanceOf(SequenceNode.class, node);

            SequenceNode sequenceNode = (SequenceNode) node;
            assertEquals(Tag.SET, sequenceNode.getTag());
            assertEquals(3, sequenceNode.getValue().size());

            ScalarNode first = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(0));
            ScalarNode second = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(1));
            ScalarNode third = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(2));

            assertEquals("one", first.getData());
            assertEquals(Tag.STR, first.getTag());
            assertEquals(2, second.getData());
            assertEquals(Tag.INT, second.getTag());
            assertEquals(true, third.getData());
            assertEquals(Tag.BOOL, third.getTag());
        }

        @Test
        public void testListSerialization() {
            List<Object> value = List.of("one", 2, true);
            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SEQUENCE, node.getNodeType());
            assertInstanceOf(SequenceNode.class, node);

            SequenceNode sequenceNode = (SequenceNode) node;
            assertEquals(Tag.SEQ, sequenceNode.getTag());
            assertEquals(3, sequenceNode.getValue().size());

            ScalarNode first = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(0));
            ScalarNode second = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(1));
            ScalarNode third = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(2));

            assertEquals("one", first.getData());
            assertEquals(Tag.STR, first.getTag());
            assertEquals(2, second.getData());
            assertEquals(Tag.INT, second.getTag());
            assertEquals(true, third.getData());
            assertEquals(Tag.BOOL, third.getTag());
        }

        @Test
        public void testEmptyListSerialization() {
            Node node = representer.representObject(List.of());

            SequenceNode sequenceNode = assertInstanceOf(SequenceNode.class, node);
            assertEquals(Tag.SEQ, sequenceNode.getTag());
            assertTrue(sequenceNode.getValue().isEmpty());
        }

    }

    @Nested
    class Maps {

        @Test
        public void testMapSerialization() {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("name", "test");
            value.put("age", 21);

            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.TREE, node.getNodeType());

            NodeTree treeNode = assertInstanceOf(NodeTree.class, node);
            assertEquals(Tag.MAP, treeNode.getTag());
            assertEquals(2, treeNode.getValue().size());

            NodeTuple firstTuple = treeNode.getValue().get(0);
            ScalarNode firstKey = assertInstanceOf(ScalarNode.class, firstTuple.getKeyNode());
            ScalarNode firstValue = assertInstanceOf(ScalarNode.class, firstTuple.getValueNode());
            assertEquals("name", firstKey.getData());
            assertEquals("test", firstValue.getData());

            NodeTuple secondTuple = treeNode.getValue().get(1);
            ScalarNode secondKey = assertInstanceOf(ScalarNode.class, secondTuple.getKeyNode());
            ScalarNode secondValue = assertInstanceOf(ScalarNode.class, secondTuple.getValueNode());
            assertEquals("age", secondKey.getData());
            assertEquals(21, secondValue.getData());
        }

        @Test
        public void testEmptyMapSerialization() {
            Node node = representer.representObject(Map.of());

            NodeTree treeNode = assertInstanceOf(NodeTree.class, node);
            assertEquals(Tag.MAP, treeNode.getTag());
            assertTrue(treeNode.getValue().isEmpty());
        }

    }

    @Nested
    class Custom {

        @Test
        public void testDirectSerialization() {
            DirectObject value = new DirectObject("data", 5);

            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals("data-5", scalarNode.getData());
            assertEquals(new Tag(DirectObject.class), scalarNode.getTag());
        }

        @Test
        public void testParentSerialization() {
            Represent found = representer.getRepresent(ParentObject.class);
            assertEquals(CustomRepresenter.RepresentByInterface.class, found.getClass());


            ParentObject value = new ParentObject("parent-data");

            Node node = representer.representObject(value);

            assertNotNull(node);
            assertEquals(NodeType.SCALAR, node.getNodeType());

            ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
            assertEquals("parent-data", scalarNode.getData());
            assertEquals(new Tag(IRepresentable.class), scalarNode.getTag());
        }

    }

    @Nested
    class EdgeCases {

        @Test
        public void testRepresentationRejectsNodeInput() {
            ScalarNode alreadyRepresented = new ScalarNode("test", Tag.STR, null, null);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> representer.representObject(alreadyRepresented)
            );

            assertEquals("Data already represented.", exception.getMessage());
        }

        @Test
        public void testRepresentationRejectsUnknownType() {
            UnknownObject value = new UnknownObject("fallback");

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> representer.representObject(value)
            );

            assertEquals("Type '" + UnknownObject.class.getName() + "' cannot be represented.", exception.getMessage());
        }
    }

    interface IRepresentable {
        String getData();
    }

    static class UnknownObject {

        private final String value;

        public UnknownObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class DirectObject {

        private final String data;
        private final int number;

        public DirectObject(String data, int number) {
            this.data = data;
            this.number = number;
        }

        public String getData() {
            return data;
        }

        public int getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object obj) {
            DirectObject bean = (DirectObject) obj;
            return data.equals(bean.getData()) && number == bean.getNumber();
        }
    }

    static class ParentObject implements IRepresentable {

        private final String data;

        public ParentObject(String data) {
            this.data = data;
        }

        @Override
        public String getData() {
            return data;
        }
    }

    static class CustomRepresenter extends SafeRepresenter {

        public CustomRepresenter() {
            super();
            setStrict(true);
            represent(DirectObject.class, new CustomRepresenter.RepresentCustom());
            representOf(IRepresentable.class, new CustomRepresenter.RepresentByInterface());
        }

        private class RepresentCustom implements Represent {

            @Override
            public @NotNull Node represent(@Nullable Object data) {
                DirectObject obj = (DirectObject) data;
                String value = obj.getData() + "-" + obj.getNumber();
                return representScalar(new Tag(DirectObject.class), value);
            }
        }

        private class RepresentByInterface implements Represent {

            @Override
            public @NotNull Node represent(@Nullable Object data) {
                IRepresentable obj = (IRepresentable) data;
                String value = obj.getData();
                return representScalar(new Tag(IRepresentable.class), value);
            }
        }

    }

}
