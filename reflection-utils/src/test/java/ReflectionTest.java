import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ConstructorAccessor;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionTest {

    @Test
    void testFindConstructor() {

        ConstructorAccessor defaultConstructor = Reflection.getConstructor(
                TestClass.class
        );
        assertNotNull(defaultConstructor);

        ConstructorAccessor constructor = Reflection.getConstructor(
                TestClass.class,
                String.class,
                int.class
        );
        assertNotNull(constructor);


        ConstructorAccessor missingConstructor = Reflection.getConstructor(
                TestClass.class,
                double.class
        );
        assertNull(missingConstructor);

    }

    @Test
    void testFindFields() {

        FieldAccessor field = Reflection.getField(
                TestClass.class,
                "name",
                null
        );
        assertNotNull(field);

        FieldAccessor typeField = Reflection.getField(
                TestClass.class,
                null,
                String.class
        );
        assertNotNull(typeField);

        FieldAccessor missingField = Reflection.getField(
                TestClass.class,
                "nonExistentField",
                null
        );
        assertNull(missingField);

    }

    @Test
    void testFindFieldsBuilder() {

        var field = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .field(
                        Reflection.field()
                                .name("name")
                                .build()
                );
        assertNotNull(field);

        var typeField = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .field(
                        Reflection.field()
                                .type(String.class)
                                .build()
                );
        assertNotNull(typeField);

        var annotatedField = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .field(
                        Reflection.field()
                                .requireAnnotation(TestAnnotation.class)
                                .build()
                );
        assertNotNull(annotatedField);

        var missingField = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .field(
                        Reflection.field()
                                .name("nonExistentField")
                                .build()
                );
        assertNull(missingField);
    }

    @Test
    void testFindMethods() {

        var method = Reflection.getMethod(
                TestClass.class,
                "testMethod"
        );
        assertNotNull(method);

        var methodWithParam = Reflection.getMethod(
                TestClass.class,
                "testMethodString",
                String.class
        );
        assertNotNull(methodWithParam);

        var missingMethod = Reflection.getMethod(
                TestClass.class,
                "nonExistentMethod"
        );
        assertNull(missingMethod);
    }

    @Test
    void testFindMethodsBuilder() {
        var method = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .method(
                        Reflection.method()
                                .name("testMethod")
                                .build()
                );
        assertNotNull(method);

        var methodWithParam = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .method(
                        Reflection.method()
                                .name("testMethodString")
                                .parameterCount(1)
                                .parameterType(0, String.class)
                                .build()
                );
        assertNotNull(methodWithParam);

        var staticMethod = Reflection.builder()
                .forClass(TestClass.class)
                .privateMembers()
                .buildAccess()
                .method(
                        Reflection.method()
                                .requireStatic()
                                .build()
                );
        assertNotNull(staticMethod);

        var annotatedStaticMethod = Reflection.builder()
                .forClass(TestClass.class)
                .privateMembers()
                .buildAccess()
                .method(
                        Reflection.method()
                                .requireStatic()
                                .requireAnnotation(TestAnnotation.class)
                                .build()
                );
        assertNotNull(annotatedStaticMethod);

        var missingMethod = Reflection.builder()
                .forClass(TestClass.class)
                .inheritedMembers()
                .buildAccess()
                .method(
                        Reflection.method()
                                .name("nonExistentMethod")
                                .build()
                );
        assertNull(missingMethod);
    }



    public static class TestClass {

        private String name;
        private static int value;

        @TestAnnotation
        private static String annotatedField;

        public TestClass() {

        }

        public TestClass(String name, int value) {

        }

        private void testMethod() {

        }

        public void testMethodString(String s) {

        }

        private static int staticMethod() {
            return 0;
        }

        @TestAnnotation
        private static int staticMethodWithParams(int a, int b) {
            return 0;
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {

    }

}
