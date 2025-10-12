import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ReflectionClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class AnnotationReflectionTest {

    private ReflectionClass reflection;

    @BeforeEach
    void setUp() {
        reflection = Reflection
                .builder()
                .forClass(AnnotationTestClass.class)
                .privateMembers()
                .build();
    }

    @Test
    void testFieldsWithAnnotations() {
        Set<Field> fields = reflection.fieldWithAnnotation(TestAnnotation.class);

        assertEquals(3, fields.size());
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("privateAnnotatedField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("protectedAnnotatedField")));
        assertTrue(fields.stream().anyMatch(f -> f.getName().equals("publicAnnotatedField")));

        assertTrue(fields.stream().noneMatch(f -> f.getName().equals("publicField")));
    }

    @Test
    void testMethodsWithAnnotations() {
        Set<Method> methods = reflection.methodsWithAnnotation(TestAnnotation.class);

        assertEquals(2, methods.size());
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("privateAnnotatedMethod")));
        assertTrue(methods.stream().anyMatch(m -> m.getName().equals("publicAnnotatedMethod")));

        assertTrue(methods.stream().noneMatch(m -> m.getName().equals("publicMethod")));
        assertTrue(methods.stream().noneMatch(m -> m.getName().equals("privateMethod")));
    }

    @Test
    void testHasAnnotation() {
        assertTrue(Reflection.hasAnnotation(AnnotationTestClass.class, TestAnnotation.class, false));
        assertFalse(Reflection.hasAnnotation(String.class, TestAnnotation.class, false));
    }

    @Test
    void testGetAnnotation() {
        TestAnnotation annotation = Reflection.getAnnotation(AnnotationTestClass.class, TestAnnotation.class, false);
        assertNotNull(annotation);
        assertEquals("class", annotation.value());
    }



    @TestAnnotation("class")
    class AnnotationTestClass {

        @TestAnnotation
        private String privateAnnotatedField;

        @TestAnnotation
        protected String protectedAnnotatedField;

        @TestAnnotation
        public String publicAnnotatedField;

        public String publicField;

        public void publicMethod() { }

        private void privateMethod() { }

        @TestAnnotation
        public void publicAnnotatedMethod() { }

        @TestAnnotation
        private void privateAnnotatedMethod() { }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
        String value() default "test";
    }

}
