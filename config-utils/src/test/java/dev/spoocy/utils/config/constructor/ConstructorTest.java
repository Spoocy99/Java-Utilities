package dev.spoocy.utils.config.constructor;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ConstructorTest {



    private final NodeConstructor nodes = new DefaultNodeConstructor(o -> {
        if(o instanceof CustomObject) {
            return ConstructorTest.CustomConstructor.CUSTOM_TAG;
        }
        return null;
    });

	private final CustomConstructor constructor = new CustomConstructor();

	@Nested
	class Constructors {

		@Test
		public void testSafeConstructorRegistersDefaults() {
			SafeConstructor safeConstructor = new SafeConstructor();

            assertNotNull(safeConstructor.getConstruct(Tag.STR));
            assertNotNull(safeConstructor.getConstruct(Tag.BOOL));
            assertNotNull(safeConstructor.getConstruct(Tag.INT));
            assertNotNull(safeConstructor.getConstruct(Tag.FLOAT));
			assertNotNull(safeConstructor.getConstruct(Tag.SET));
			assertNotNull(safeConstructor.getConstruct(Tag.SEQ));
			assertNotNull(safeConstructor.getConstruct(Tag.MAP));
		}

		@Test
		public void testCustomConstructorKeepsDefaultsAndRegistersCustomType() {
			CustomConstructor customConstructor = new CustomConstructor();

            assertNotNull(customConstructor.getConstruct(Tag.STR));
            assertNotNull(customConstructor.getConstruct(Tag.BOOL));
            assertNotNull(customConstructor.getConstruct(Tag.INT));
            assertNotNull(customConstructor.getConstruct(Tag.FLOAT));
			assertNotNull(customConstructor.getConstruct(Tag.SET));
			assertNotNull(customConstructor.getConstruct(Tag.SEQ));
			assertNotNull(customConstructor.getConstruct(Tag.MAP));
			assertNotNull(customConstructor.getConstruct(new Tag(CustomObject.class)));
		}
	}

	@Nested
	class Primitives {

		@Test
		public void testString() {
			String value = "Hello, World!";

            // Node
			Node node = nodes.construct(value);
			assertNotNull(node);
			assertEquals(NodeType.SCALAR, node.getNodeType());

			ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
			assertEquals(value, scalarNode.getData());
			assertEquals(Tag.STR, scalarNode.getTag());

            // reconstruction
            Object constructed = constructor.constructObject(node);
            assertEquals(value, constructed);
		}

		@Test
		public void testNull() {
            // Node
			Node node = nodes.construct(null);
			assertNotNull(node);
			assertEquals(NodeType.SCALAR, node.getNodeType());

			ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
			assertNull(scalarNode.getData());
			assertEquals(Tag.NULL, scalarNode.getTag());

            // reconstruction
            Object constructed = constructor.constructObject(node);
            assertNull(constructed);
		}
	}

	@Nested
	class Collections {

		@Test
		public void testListSerialization() {
            // Node
			List<Object> value = List.of("one", 2L, true);
			Node node = nodes.construct(value);

			assertNotNull(node);
			assertEquals(NodeType.SEQUENCE, node.getNodeType());

			SequenceNode sequenceNode = assertInstanceOf(SequenceNode.class, node);
			assertEquals(Tag.SEQ, sequenceNode.getTag());
			assertEquals(3, sequenceNode.getValue().size());

			ScalarNode first = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(0));
			ScalarNode second = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(1));
			ScalarNode third = assertInstanceOf(ScalarNode.class, sequenceNode.getValue().get(2));

			assertEquals("one", first.getData());
			assertEquals(Tag.STR, first.getTag());
			assertEquals(2L, second.getData());
			assertEquals(Tag.INT, second.getTag());
			assertEquals(true, third.getData());
			assertEquals(Tag.BOOL, third.getTag());


            // reconstruction
            Object constructed = constructor.constructObject(node);
            List<?> constructedList = assertInstanceOf(List.class, constructed);
            assertEquals(value.size(), constructedList.size());
            for (int i = 0; i < value.size(); i++) {
                assertEquals(value.get(i), constructedList.get(i));
            }
		}

		@Test
		public void testEmptyListSerialization() {
            // Node
			Node node = nodes.construct(List.of());

			SequenceNode sequenceNode = assertInstanceOf(SequenceNode.class, node);
			assertEquals(Tag.SEQ, sequenceNode.getTag());
			assertTrue(sequenceNode.getValue().isEmpty());

            // reconstruction
            Object constructed = constructor.constructObject(node);
            List<?> constructedList = assertInstanceOf(List.class, constructed);
            assertTrue(constructedList.isEmpty());
		}
	}

	@Nested
	class Maps {

		@Test
		public void testMapSerialization() {
			Map<String, Object> value = new LinkedHashMap<>();
			value.put("name", "test");
			value.put("age", 21.1D);

            // Node
			Node node = nodes.construct(value);

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
			assertEquals(21.1D, secondValue.getData());

            // reconstruction
            Object constructed = constructor.constructObject(node);
            Map<?, ?> constructedMap = assertInstanceOf(Map.class, constructed);

            for (Map.Entry<String, Object> entry : value.entrySet()) {
                String key = entry.getKey();
                assertTrue(constructedMap.containsKey(key));

                Object val = constructedMap.get(entry.getKey());
                assertEquals(entry.getValue(), val);
            }
		}

		@Test
		public void testEmptyMapSerialization() {
            // Node
			Node node = nodes.construct(Map.of());

			NodeTree treeNode = assertInstanceOf(NodeTree.class, node);
			assertEquals(Tag.MAP, treeNode.getTag());
			assertTrue(treeNode.getValue().isEmpty());

            // reconstruction
            Object constructed = constructor.constructObject(node);
            assertEquals(Map.of(), constructed);
		}
	}

	@Nested
	class Custom {

		@Test
		public void testSingleSerialization() {
			ScalarNode node = new ScalarNode("data-5", new Tag(CustomObject.class), null, null);

            // reconstruction
            Object constructed = constructor.constructObject(node);
            CustomObject obj = assertInstanceOf(CustomObject.class, constructed);
            assertEquals(obj, constructed);
		}

        @Test
		public void testListSerialization() {
            Node node = new SequenceNode(Tag.SEQ,
                    List.of(
                            new ScalarNode("data-1", new Tag(CustomObject.class), null, null),
                            new ScalarNode("data-2", new Tag(CustomObject.class), null, null)
                    ),
                    List.of(),
                    List.of()

            );

            // reconstruction
            Object constructed = constructor.constructObject(node);
            List<?> constructedList = assertInstanceOf(List.class, constructed);

            CustomObject obj1 = assertInstanceOf(CustomObject.class, constructedList.get(0));
            assertEquals("data", obj1.getData());
            assertEquals(1, obj1.getNumber());

            CustomObject obj2 = assertInstanceOf(CustomObject.class, constructedList.get(1));
            assertEquals("data", obj2.getData());
            assertEquals(2, obj2.getNumber());
		}

        @Test
		public void testMapSerialization() {
            Node node = new NodeTree(Tag.MAP,
                    List.of(
                            NodeTuple.of(
                                    new ScalarNode("data1", Tag.STR, null, null),
                                    new ScalarNode("data-1", new Tag(CustomObject.class), null, null)
                            ),
                            NodeTuple.of(
                                    new ScalarNode("data2", Tag.STR, null, null),
                                    new ScalarNode("data-2", new Tag(CustomObject.class), null, null)
                            )
                    ),
                    List.of(),
                    List.of()

            );

            // reconstruction
            Object constructed = constructor.constructObject(node);
            Map<?, ?> constructedMap = assertInstanceOf(Map.class, constructed);

            CustomObject obj1 = assertInstanceOf(CustomObject.class, constructedMap.get("data1"));
            assertEquals("data", obj1.getData());
            assertEquals(1, obj1.getNumber());

            CustomObject obj2 = assertInstanceOf(CustomObject.class, constructedMap.get("data2"));
            assertEquals("data", obj2.getData());
            assertEquals(2, obj2.getNumber());
        }

        @Test
		public void testMapOverwriteSerialization() {
            Node node = new NodeTree(Tag.MAP,
                    List.of(
                            NodeTuple.of(
                                    new ScalarNode("==", Tag.STR, null, null),
                                    new ScalarNode("CustomObject", Tag.STR, null, null)
                            ),
                            NodeTuple.of(
                                    new ScalarNode("data", Tag.STR, null, null),
                                    new ScalarNode("test", Tag.STR, null, null)
                            ),
                            NodeTuple.of(
                                    new ScalarNode("number", Tag.STR, null, null),
                                    new ScalarNode("1", Tag.STR, null, null)
                            )
                    ),
                    List.of(),
                    List.of()

            );

            // reconstruction
            Object constructed = constructor.constructObject(node);
            CustomObject constructedObj = assertInstanceOf(CustomObject.class, constructed);
            assertEquals("test", constructedObj.getData());
            assertEquals(1, constructedObj.getNumber());
        }
    }

	@Nested
	class EdgeCases {

		@Test
		public void testFallbackToTypeTagForUnknownObject() {

			UnknownObject value = new UnknownObject("fallback");
			Node node = nodes.construct(value);

			ScalarNode scalarNode = assertInstanceOf(ScalarNode.class, node);
			assertSame(value, scalarNode.getData());
			assertEquals(new Tag(UnknownObject.class), scalarNode.getTag());
		}

		@Test
		public void testConstructionRejectsNodeInput() {
			ScalarNode alreadyConstructed = new ScalarNode("test", Tag.STR, null, null);

			IllegalArgumentException exception = assertThrows(
					IllegalArgumentException.class,
					() -> nodes.construct(alreadyConstructed)
			);

			assertEquals("Data already constructed.", exception.getMessage());
		}
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

	static class CustomObject {

		private final String data;
		private final int number;

		public CustomObject(String data, int number) {
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
			CustomObject bean = (CustomObject) obj;
			return data.equals(bean.getData()) && number == bean.getNumber();
		}
	}

	static class CustomConstructor extends SafeConstructor {

		public static final Tag CUSTOM_TAG = new Tag(CustomObject.class);

		public CustomConstructor() {
			super();
            this.construct(Tag.MAP, new CustomMapConstructor());
			this.construct(CUSTOM_TAG, new CustomObjConstructor());
		}

        public Object constructObject(@NotNull Node node) {
            return super.constructObject(node);
        }

        private class CustomMapConstructor extends MapConstructor {
            @Override
            public @Nullable Object construct(@Nullable Node node) {
                Map<Object, Object> map = (Map<Object, Object>) super.construct(node);

                if(map.containsKey("==") && map.get("==").equals("CustomObject")) {
                    return new CustomObject(
                            map.get("data").toString(),
                            Integer.parseInt(map.get("number").toString())
                    );
                }

                return map;
            }
        }

		private class CustomObjConstructor implements Construct {

            @Override
            public @Nullable Object construct(@Nullable Node node) {
                String data = ((ScalarNode) node).getData().toString();
                return new CustomObject(
                        data.substring(0, data.indexOf("-")),
                        Integer.parseInt(data.substring(data.indexOf("-") + 1))
                );
            }
        }
	}





}
