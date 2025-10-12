package dev.spoocy.utils.common.misc;

import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class NumberConversion {

    private NumberConversion() { }

    public static int toInt(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0;
    }

    public static float toFloat(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }

        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0.0F;
    }

    public static double toDouble(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        }

        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0.0;
    }

    public static long toLong(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }

        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0L;
    }

    public static short toShort(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number)object).shortValue();
        }

        try {
            return Short.parseShort(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0;
    }

    public static byte toByte(@Nullable Object object) {
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }

        try {
            return Byte.parseByte(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) { }
        return 0;
    }

    public static boolean toBoolean(@Nullable Object object) {
        if (object instanceof Boolean) {
            return (Boolean)object;
        }

        if (object instanceof Number) {
            return ((Number)object).intValue() != 0;
        }

        String str = object.toString();
        return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("on");
    }

    public static <T> T convert(Object o, Class<T> clazz) {
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(toInt(o));
        }

        if (clazz == float.class || clazz == Float.class) {
            return (T) Float.valueOf(toFloat(o));
        }

        if (clazz == double.class || clazz == Double.class) {
            return (T) Double.valueOf(toDouble(o));
        }

        if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(toLong(o));
        }

        if (clazz == short.class || clazz == Short.class) {
            return (T) Short.valueOf(toShort(o));
        }

        if (clazz == byte.class || clazz == Byte.class) {
            return (T) Byte.valueOf(toByte(o));
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return (T) Boolean.valueOf(toBoolean(o));
        }

        return null;
    }

    public static boolean isFinite(double d) {
        return Math.abs(d) <= Double.MAX_VALUE;
    }

    public static boolean isFinite(float f) {
        return Math.abs(f) <= Float.MAX_VALUE;
    }

    public static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int ceil(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor + (int) ( ~ Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int round(double num) {
        return floor(num + 0.5);
    }

    public static double square(double num) {
        return num * num;
    }
}
