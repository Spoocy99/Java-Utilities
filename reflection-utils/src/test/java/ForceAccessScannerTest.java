import dev.spoocy.utils.reflection.scanner.Scanner;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ForceAccessScannerTest {

    private static final Scanner scanner = Scanner.FORCE_ACCESS;

    @Test
    void testVisible() {
        assertTrue(scanner.isVisible(Modifier.PUBLIC));
        assertTrue(scanner.isVisible(Modifier.PRIVATE));
        assertTrue(scanner.isVisible(Modifier.PROTECTED));
    }

    @Test
    void testClasses() {
        Set<Class<?>> classes = scanner.classes(BaseTestClass.class);
        assertEquals(1, classes.size());
        assertTrue(classes.contains(BaseTestClass.class));
    }

    @Test
    void testFields() {
        Set<Field> fields = scanner.fields(BaseTestClass.class);

        assertEquals(6, fields.size());
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("PUBLIC_CONSTANT")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("publicField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("PROTECTED_CONSTANT")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("PRIVATE_CONSTANT")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("privateField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("protectedField")));
    }

    @Test
    void testMethods() {
        Set<Method> methods = scanner.methods(BaseTestClass.class);

        assertEquals(3, methods.size());
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("publicMethod")));
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("privateMethod")));
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("protectedMethod")));
    }

    @Test
    void testConstructors() {
        Set<Constructor<?>> constructors = scanner.constructors(BaseTestClass.class);

        assertEquals(3, constructors.size());
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 0));
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class));
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0] == int.class));
    }

    @Test
    void testLookupConstructor() {
        Constructor<?> defaultConstructor = scanner.lookupConstructor(BaseTestClass.class);
        assertNotNull(defaultConstructor);

        Constructor<?> paramConstructor = scanner.lookupConstructor(BaseTestClass.class, String.class);
        assertNotNull(paramConstructor);

        Constructor<?> privateConstructor = scanner.lookupConstructor(BaseTestClass.class, int.class);
        assertNotNull(privateConstructor);
    }

    @Test
    void testLookupMethods() {
        Set<Method> methods = scanner.lookupMethods(BaseTestClass.class, (method, source) -> method.getName().equals("privateMethod"));
        assertFalse(methods.isEmpty());
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("privateMethod")));
    }

    @Test
    void testLookupFields() {
        Set<Field> fields = scanner.lookupFields(BaseTestClass.class, (field, source) -> field.getName().equals("privateField"));
        assertFalse(fields.isEmpty());
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("privateField")));
    }

}
