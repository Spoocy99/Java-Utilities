import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ConstructorAccessor;
import org.junit.jupiter.api.Test;

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


    public static class TestClass {

        public TestClass() {

        }

        public TestClass(String name, int value) {

        }


    }
}
