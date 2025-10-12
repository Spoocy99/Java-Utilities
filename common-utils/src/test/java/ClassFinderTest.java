import dev.spoocy.utils.common.misc.ClassFinder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassFinderTest {

    @Test
    void testCallingClassName() {
        String className = helperCallingClassName();
        assertEquals(this.getClass().getName(), className);
    }

    // Helper method to add an extra stack frame
    private String helperCallingClassName() {
        return ClassFinder.callingClassName();
    }

    @Test
    void testCallingClassNameWithDepth() {
        String className = helperCallingClassName(1);
        assertEquals(this.getClass().getName(), className);
    }

    private String helperCallingClassName(int depth) {
        return ClassFinder.callingClassName(depth);
    }

    @Test
    void testCaller() {
        Class<?> callerClass = helperCaller();
        assertEquals(this.getClass(), callerClass);
    }

    // Helper method to add an extra stack frame
    private Class<?> helperCaller() {
        return ClassFinder.caller();
    }

    @Test
    void testCallerWithDepth() {
        Class<?> callerClass = helperCaller(2);
        assertEquals(this.getClass(), callerClass);
    }

    // Helper method to add an extra stack frame
    private Class<?> helperCaller(int depth) {
        return ClassFinder.caller(depth);
    }

    @Test
    void testGetClassContext() {
        ClassFinder finder = new ClassFinder();
        Class<?>[] context = finder.getClassContext();
        assertNotNull(context);
        assertTrue(context.length > 0);
    }

    @Test
    void testGetCallingClass() {
        ClassFinder finder = new ClassFinder();
        Class<?> callingClass = finder.getCallingClass(0);
        assertEquals(ClassFinder.class, callingClass);
    }


}
