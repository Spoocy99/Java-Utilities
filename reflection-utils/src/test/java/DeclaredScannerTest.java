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

public class DeclaredScannerTest {

    private static final Scanner scanner = Scanner.PUBLIC_DECLARED;

    @Test
    void testVisible() {
        assertTrue(scanner.isVisible(Modifier.PUBLIC));
        assertFalse(scanner.isVisible(Modifier.PRIVATE));
        assertFalse(scanner.isVisible(Modifier.PROTECTED));
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

        assertEquals(2, fields.size());
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("PUBLIC_CONSTANT")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("publicField")));

        // Ensure non-public fields are not included
        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("PROTECTED_CONSTANT")));
        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("PRIVATE_CONSTANT")));
        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("privateField")));
        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("protectedField")));
    }

    @Test
    void testMethods() {
        Set<Method> methods = scanner.methods(BaseTestClass.class);

        assertEquals(1, methods.size());
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("publicMethod")));
        assertTrue(methods.stream().noneMatch(m -> m.getName().equals("privateMethod")));

        // Ensure non-public methods are not included
        assertTrue(methods.stream().noneMatch(m -> m.getName().equals("protectedMethod")));
    }

    @Test
    void testConstructors() {
        Set<Constructor<?>> constructors = scanner.constructors(BaseTestClass.class);

        assertEquals(2, constructors.size());
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 0));
        assertTrue(constructors.stream().anyMatch(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0] == String.class));

        // Ensure non-public constructors are not included
        assertTrue(constructors.stream().noneMatch(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0] == int.class));
    }

    @Test
    void testLookupConstructor() {
        // Lookup default constructor
        Constructor<?> defaultConstructor = scanner.lookupConstructor(BaseTestClass.class);
        assertNotNull(defaultConstructor);

        // Lookup constructor with parameters
        Constructor<?> paramConstructor = scanner.lookupConstructor(BaseTestClass.class, String.class);
        assertNotNull(paramConstructor);

        // Ensure non-public constructor is not found
        Constructor<?> privateConstructor = scanner.lookupConstructor(BaseTestClass.class, int.class);
        assertNull(privateConstructor);
    }

    @Test
    void testLookupMethods() {
        Set<Method> methods = scanner.lookupMethods(BaseTestClass.class, (method, source) -> method.getName().equals("publicMethod"));
        assertEquals(1, methods.size());
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("publicMethod")));

        Set<Method> privateMethods = scanner.lookupMethods(BaseTestClass.class, (method, source) -> method.getName().equals("privateMethod"));
        assertTrue(privateMethods.isEmpty());
    }

    @Test
    void testLookupFields() {
        Set<Field> fields = scanner.lookupFields(BaseTestClass.class, (field, source) -> field.getName().equals("publicField"));
        assertEquals(1, fields.size());
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("publicField")));

        Set<Field> privateFields = scanner.lookupFields(BaseTestClass.class, (field, source) -> field.getName().equals("privateField"));
        assertTrue(privateFields.isEmpty());
    }

}
