package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.nodes.Node;
import dev.spoocy.utils.config.nodes.ScalarNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SafeRepresenter extends BaseRepresenter {

    public SafeRepresenter() {
        super();
        representNull(new NullRepresenter());

        represent(String.class, new StringRepresenter());
        represent(Boolean.class, new BooleanRepresenter());

        Represent primitiveArrayRepresenter = new PrimitiveArrayRepresenter();
        represent(byte[].class, primitiveArrayRepresenter);
        represent(short[].class, primitiveArrayRepresenter);
        represent(int[].class, primitiveArrayRepresenter);
        represent(long[].class, primitiveArrayRepresenter);
        represent(float[].class, primitiveArrayRepresenter);
        represent(double[].class, primitiveArrayRepresenter);
        represent(char[].class, primitiveArrayRepresenter);
        represent(boolean[].class, primitiveArrayRepresenter);

        representOf(Number.class, new NumberRepresenter());
        representOf(Enum.class, new RepresentEnum());

        representOf(Object[].class, new ArrayRepresenter());
        representOf(Map.class, new MapRepresenter());
        representOf(Set.class, new SetRepresenter());
        representOf(List.class, new ListRepresenter());
        representOf(Iterator.class, new IteratorRepresenter());
    }

    protected class NullRepresenter implements Represent {
        @Override
        public @NotNull Node represent(@Nullable Object data) {
            return ScalarNode.nullValue();
        }
    }

    protected class StringRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof String)) {
                throw new IllegalArgumentException("Tried to represent non-string data.");
            }
            return representScalar(Tag.STR, data);
        }

    }

    protected class BooleanRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Boolean)) {
                throw new IllegalArgumentException("Tried to represent non-boolean data.");
            }
            return representScalar(Tag.BOOL, data);
        }
    }

    protected class NumberRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Number)) {
                throw new IllegalArgumentException("Tried to represent non-number data.");
            }

            Class<?> type = data.getClass();

            if (Tag.INT.isCompatible(type)) {
                return representScalar(Tag.INT, data);
            }

            if (Tag.FLOAT.isCompatible(type)) {
                return representScalar(Tag.FLOAT, data);
            }

            if (Tag.BOOL.isCompatible(type)) {
                return representScalar(Tag.BOOL, data);
            }

            throw new IllegalArgumentException("No compatible tag found for type: " + type.getName());
        }
    }

    protected class RepresentEnum implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Enum<?>)) {
                throw new IllegalArgumentException("Tried to represent non-enum data.");
            }
            Tag tag = new Tag(data.getClass());
            return representScalar(tag, ((Enum<?>) data).name());
        }

    }

    protected class IteratorRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Iterator<?>)) {
                throw new IllegalArgumentException("Tried to represent non-iterator data.");
            }

            Iterator<Object> wrapped = (Iterator<Object>) data;
            return representSequence(Tag.SEQ, new IteratorWrapper(wrapped));
        }
    }

    protected class ArrayRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data.getClass().isArray())) {
                throw new IllegalArgumentException("Tried to represent non-array data.");
            }

            Object[] array = (Object[]) data;
            List<Object> list = Arrays.asList(array);
            return representSequence(Tag.SEQ, list);
        }
    }

    protected class PrimitiveArrayRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            Class<?> type = data.getClass().getComponentType();

            if (byte.class == type) {
                return representSequence(Tag.SEQ, asByteList(data));
            } else if (short.class == type) {
                return representSequence(Tag.SEQ, asShortList(data));
            } else if (int.class == type) {
                return representSequence(Tag.SEQ, asIntList(data));
            } else if (long.class == type) {
                return representSequence(Tag.SEQ, asLongList(data));
            } else if (float.class == type) {
                return representSequence(Tag.SEQ, asFloatList(data));
            } else if (double.class == type) {
                return representSequence(Tag.SEQ, asDoubleList(data));
            } else if (char.class == type) {
                return representSequence(Tag.SEQ, asCharList(data));
            } else if (boolean.class == type) {
                return representSequence(Tag.SEQ, asBooleanList(data));
            }

            throw new IllegalArgumentException("Unexpected primitive '" + type.getCanonicalName() + "'");
        }

        @NotNull
        private List<Byte> asByteList(@NotNull Object in) {
            byte[] array = (byte[]) in;
            List<Byte> list = new ArrayList<>(array.length);
            for (byte b : array) {
                list.add(b);
            }
            return list;
        }

        @NotNull
        private List<Short> asShortList(@NotNull Object in) {
            short[] array = (short[]) in;
            List<Short> list = new ArrayList<>(array.length);
            for (short value : array) {
                list.add(value);
            }
            return list;
        }

        @NotNull
        private List<Integer> asIntList(@NotNull Object in) {
            int[] array = (int[]) in;
            List<Integer> list = new ArrayList<>(array.length);
            for (int j : array) {
                list.add(j);
            }
            return list;
        }

        @NotNull
        private List<Long> asLongList(@NotNull Object in) {
            long[] array = (long[]) in;
            List<Long> list = new ArrayList<>(array.length);
            for (long l : array) {
                list.add(l);
            }
            return list;
        }

        @NotNull
        private List<Float> asFloatList(@NotNull Object in) {
            float[] array = (float[]) in;
            List<Float> list = new ArrayList<>(array.length);
            for (float v : array) {
                list.add(v);
            }
            return list;
        }

        @NotNull
        private List<Double> asDoubleList(@NotNull Object in) {
            double[] array = (double[]) in;
            List<Double> list = new ArrayList<>(array.length);
            for (double v : array) {
                list.add(v);
            }
            return list;
        }

        @NotNull
        private List<Character> asCharList(Object in) {
            char[] array = (char[]) in;
            List<Character> list = new ArrayList<>(array.length);
            for (char c : array) {
                list.add(c);
            }
            return list;
        }

        @NotNull
        private List<Boolean> asBooleanList(Object in) {
            boolean[] array = (boolean[]) in;
            List<Boolean> list = new ArrayList<>(array.length);
            for (boolean b : array) {
                list.add(b);
            }
            return list;
        }
    }

    protected class SetRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Set<?>)) {
                throw new IllegalArgumentException("Tried to represent non-set data.");
            }

            Set<?> set = (Set<?>) data;
            return representSequence(Tag.SET, set);
        }
    }

    protected class ListRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof List<?>)) {
                throw new IllegalArgumentException("Tried to represent non-list data.");
            }

            List<?> list = (List<?>) data;
            return representSequence(Tag.SEQ, list);
        }
    }

    protected class MapRepresenter implements Represent {

        @Override
        public @NotNull Node represent(@Nullable Object data) {
            if (!(data instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("Tried to represent non-map data.");
            }

            Map<?, ?> map = (Map<?, ?>) data;
            return representMapping(map);
        }
    }

    private static class IteratorWrapper implements Iterable<Object> {

        private final Iterator<Object> iterator;

        public IteratorWrapper(@NotNull Iterator<Object> iterator) {
            this.iterator = iterator;
        }

        @Override
        public @NotNull Iterator<Object> iterator() {
            return iterator;
        }
    }

}
