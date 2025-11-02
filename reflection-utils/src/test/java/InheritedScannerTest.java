import dev.spoocy.utils.reflection.scanner.Scanner;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class InheritedScannerTest {

    private static final Scanner scanner = Scanner.INHERITED;

    @Test
    void testVisible() {
        assertTrue(scanner.isVisible(Modifier.PUBLIC));
        assertTrue(scanner.isVisible(Modifier.PROTECTED));
        assertTrue(scanner.isVisible(Modifier.PRIVATE));
    }

    @Test
    void testClasses() {
        Set<Class<?>> classes = scanner.classes(DerivedTestClass.class);
        assertTrue(classes.contains(DerivedTestClass.class));
        assertTrue(classes.contains(BaseTestClass.class));
    }

    @Test
    void testFields() {
        Set<Field> fields = scanner.fields(DerivedTestClass.class);

        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("publicField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("protectedField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("derivedField")));
    }

    @Test
    void testMethods() {
        Set<Method> methods = scanner.methods(DerivedTestClass.class);

        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("publicMethod")));
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("protectedMethod")));
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("derivedMethod")));
    }

    @Test
    void testConstructors() {
        Set<Constructor<?>> constructors = scanner.constructors(DerivedTestClass.class);

        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 0));
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class));
    }

    @Test
    void testLookupConstructor() {
        Constructor<?> notFoundConstructor = scanner.lookupConstructor(DerivedTestClass.class, Integer.class);
        assertNull(notFoundConstructor);

        Constructor<?> constructor = scanner.lookupConstructor(DerivedTestClass.class);
        assertNotNull(constructor);

        Constructor<?> publicConstructor = scanner.lookupConstructor(DerivedTestClass.class, String.class);
        assertNotNull(publicConstructor);

        Constructor<?> protectedConstructor = scanner.lookupConstructor(DerivedTestClass.class, Runnable.class);
        assertNotNull(protectedConstructor);

        Constructor<?> privateConstructor = scanner.lookupConstructor(DerivedTestClass.class, String.class, String.class);
        assertNotNull(privateConstructor);
    }

    @Test
    void testLookupMethods() {
        Set<Method> methods = scanner.lookupMethods(DerivedTestClass.class, (method, source) -> method.getName().equals("publicMethod"));
        assertFalse(methods.isEmpty());
    }

    @Test
    void testLookupFields() {
        Set<Field> fields = scanner.lookupFields(DerivedTestClass.class, (field, source) -> field.getName().equals("publicField"));
        assertFalse(fields.isEmpty());
    }

    public static class BaseTestClass {
        public String publicField;
        protected String protectedField;
        public void publicMethod() {}
        protected void protectedMethod() {}
    }

    public static class DerivedTestClass extends BaseTestClass {
        public String derivedField;

        public DerivedTestClass() {}
        public DerivedTestClass(String s) {}

        protected DerivedTestClass(Runnable runnable) {}

        private DerivedTestClass(String s1, String s2) {}

        public void derivedMethod() {}
    }

}
