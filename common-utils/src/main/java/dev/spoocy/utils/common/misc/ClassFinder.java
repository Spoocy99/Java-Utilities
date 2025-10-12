package dev.spoocy.utils.common.misc;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassFinder extends SecurityManager {

    private static final ClassFinder INSTANCE = new ClassFinder();

    public static String callingClassName() {
        return callingClassName(2);
    }

    public static String callingClassName(final int depth) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length <= depth) {
            throw new IllegalArgumentException("Not enough stack elements to skip " + depth + " elements");
        } else {
            return elements[depth + 2].getClassName();
        }
    }

    /**
     * Returns the class of the method that called this method.
     *
     * @return the caller of this method.
     */
    public static Class<?> caller() {
        return caller(3);
    }

    /**
     * Returns the class of the method that called this method.
     *
     * @param depth  the depth of the calling class.
     * @return the caller of this method.
     */
    public static Class<?> caller(int depth) {
        try {
            return INSTANCE.getCallingClass(depth + 2);
        } catch (Exception e) {
            return null;
        }
    }

    public static Class<?> finalCaller() {
        try {
            Class<?>[] stack = INSTANCE.getClassContext();
            return stack[stack.length - 1];
        } catch (Exception e) {
            return null;
        }
    }

    public ClassFinder() {
        super();
    }

    /**
     * Returns the current execution stack as an array of classes.
     * <p>
     * The length of the array is the number of methods on the execution
     * stack. The element at index {@code 0} is the class of the
     * currently executing method, the element at index {@code 1} is
     * the class of that method's caller, and so on.
     *
     * @return the execution stack.
     */
    public Class<?>[] getClassContext() {
        // method is normally protected
        return super.getClassContext();
    }

    public Class<?> getCallingClass(int depth) {
        try {
            return getClassContext()[depth];
        } catch (Exception e) {
            return null;
        }
    }

}
