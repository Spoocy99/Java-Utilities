import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassAccessTest {

    @Test
    void testConstants() {

        Set<FieldAccessor> fields = Reflection.builder()
                .forClass(BaseTestClass.class)
                .privateMembers()
                .buildAccess()
                .constants();

        assertEquals(3, fields.size());

        FieldAccessor privateField = fields.stream().filter(f -> f.getField().getName().equals("PRIVATE_CONSTANT")).findFirst().orElse(null);
        assertNotNull(privateField);
        assertEquals("Base", privateField.get(null));

        FieldAccessor protectedField = fields.stream().filter(f -> f.getField().getName().equals("PROTECTED_CONSTANT")).findFirst().orElse(null);
        assertNotNull(protectedField);
        assertEquals("Class", protectedField.get(null));

        FieldAccessor publicField = fields.stream().filter(f -> f.getField().getName().equals("PUBLIC_CONSTANT")).findFirst().orElse(null);
        assertNotNull(publicField);
        assertEquals("Test", publicField.get(null));
    }

    @Test
    void testPublicField() {

        BaseTestClass impl = new BaseTestClass();

        Set<FieldAccessor> fields = Reflection.builder()
                .forClass(BaseTestClass.class)
                .publicMembers()
                .buildAccess()
                .fields(
                        Reflection.field()
                                .anyName()
                                .type(int.class)
                                .requirePublic()
                                .build()
                );

        assertEquals(1, fields.size());

        FieldAccessor field = fields.iterator().next();
        assertEquals("baseField", field.getField().getName());
        assertEquals(int.class, field.getField().getType());
        assertEquals(42, field.get(impl));

    }

    @Test
    void testPrivateField() {

        BaseTestClass impl = new BaseTestClass();

        Set<FieldAccessor> fields = Reflection.builder()
                .forClass(BaseTestClass.class)
                .privateMembers()
                .buildAccess()
                .fields(
                        Reflection.field()
                                .name("privateField")
                                .build()
                );

        assertEquals(1, fields.size());

        FieldAccessor field = fields.iterator().next();
        assertEquals("privateField", field.getField().getName());
        assertEquals(String.class, field.getField().getType());
        assertEquals("private", field.get(impl));

    }

    @Test
    void testInheritedField() {

        ExtendedTestClass impl = new ExtendedTestClass();

        Set<FieldAccessor> fields = Reflection.builder()
                .forClass(ExtendedTestClass.class)
                .inheritedMembers()
                .buildAccess()
                .fields(
                        Reflection.field()
                                .name("privateField")
                                .type(String.class)
                                .build()
                );

        assertEquals(1, fields.size());

        FieldAccessor field = fields.iterator().next();
        assertEquals("privateField", field.getField().getName());
        assertEquals(String.class, field.getField().getType());
        assertEquals("private", field.get(impl));

    }


    @Test
    void testPublicMethod() {
        ExtendedTestClass impl = new ExtendedTestClass();

        Set<MethodAccessor> methods = Reflection.builder()
                .forClass(ExtendedTestClass.class)
                .publicMembers()
                .buildAccess()
                .methods(
                        Reflection.method()
                                .name("publicMethod")
                                .build()
                );

        assertEquals(1, methods.size());

        MethodAccessor method = methods.iterator().next();
        assertEquals("publicMethod", method.getMethod().getName());
        assertEquals(String.class, method.getMethod().getReturnType());
        assertEquals("publicMethod", method.invoke(impl));
    }

    @Test
    void testPrivateMethod() {
        BaseTestClass impl = new BaseTestClass();

        Set<MethodAccessor> methods = Reflection.builder()
                .forClass(BaseTestClass.class)
                .privateMembers()
                .buildAccess()
                .methods(
                        Reflection.method()
                                .name("privateMethod")
                                .parameterCount(0)
                                .build()
                );

        assertEquals(1, methods.size());

        MethodAccessor method = methods.iterator().next();
        assertEquals("privateMethod", method.getMethod().getName());
        assertEquals(String.class, method.getMethod().getReturnType());
        assertEquals("privateMethod", method.invoke(impl));
    }

    @Test
    void testInheritedMethod() {
        ExtendedTestClass impl = new ExtendedTestClass();

        Set<MethodAccessor> methods = Reflection.builder()
                .forClass(ExtendedTestClass.class)
                .inheritedMembers()
                .buildAccess()
                .methods(
                        Reflection.method()
                                .name("baseMethod")
                                .returnType(String.class)
                                .build()
                );

        assertEquals(1, methods.size());

        MethodAccessor method = methods.iterator().next();
        assertEquals("baseMethod", method.getMethod().getName());
        assertEquals(String.class, method.getMethod().getReturnType());
        assertEquals("baseMethod", method.invoke(impl));
    }


    public static class BaseTestClass {
        private static final String PRIVATE_CONSTANT = "Base";
        protected static final String PROTECTED_CONSTANT = "Class";
        public static final String PUBLIC_CONSTANT = "Test";

        public int baseField = 42;
        private String privateField = "private";
        public String baseMethod() { return "baseMethod"; }
        private String privateMethod() { return "privateMethod"; }
    }

    public static class ExtendedTestClass extends BaseTestClass {
        public String publicField;
        public String publicMethod() { return "publicMethod"; }
    }



}
