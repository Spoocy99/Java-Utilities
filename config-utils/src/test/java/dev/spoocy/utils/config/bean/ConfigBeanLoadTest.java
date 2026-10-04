package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.types.MemoryConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class ConfigBeanLoadTest extends ConfigBeanTest {

    @Nested
    class Errors {

        @Test
        void rejectsClassesWithoutConfigSource() {
            MemoryConfig config = new MemoryConfig();
            assertThrows(IllegalArgumentException.class, () -> LOADER.load(UnannotatedBean.class, config, LoadStrategy.JUST_LOAD));
        }

        @Test
        void ignoreFinalFields() {
            MemoryConfig config = new MemoryConfig();
            config.set("value", "test123");

            FinalValueBean bean = LOADER.load(FinalValueBean.class, config, LoadStrategy.JUST_LOAD);
            assertEquals("test", bean.value);
        }

    }

    public static class UnannotatedBean {
        public String value;
    }

    @ConfigSource()
    public static class FinalValueBean {

        @Property("value")
        public final String value = "test";

    }

    @Nested
    class Primitives {

        @Test
        void loadsPrimitives() {
            MemoryConfig config = new MemoryConfig();
            config.set("str", "test123");
            config.set("num", 12);

            PrimitivesBean bean = LOADER.load(PrimitivesBean.class, config, LoadStrategy.JUST_LOAD);

            // overwritten by config
            assertEquals("test123", bean.str);
            assertEquals(12, bean.num);

            // default value
            assertFalse(bean.bool);
        }

    }

    @ConfigSource()
    public static class PrimitivesBean {

        @Property("str")
        public String str = "test";

        @Property("num")
        public int num = 1;

        @Property("bool")
        public boolean bool = false;
    }

    @Nested
    class Collections {

        @Test
        void loadsCollections() {
            MemoryConfig config = new MemoryConfig();
            config.set("list", List.of("a", "b", "c"));
            config.set("map", Map.of("key1", "value1", "key2", 42));

            CollectionsBean bean = LOADER.load(CollectionsBean.class, config, LoadStrategy.JUST_LOAD);
            assertEquals(List.of("a", "b", "c"), bean.list);
            assertEquals(Map.of("key1", "value1", "key2", 42), bean.map);
        }
    }

    @ConfigSource()
    public static class CollectionsBean {

        @Property("list")
        public List<String> list = new ArrayList<>();

        @Property("map")
        public Map<String, Object> map = new LinkedHashMap<>();
    }

    @Nested
    class NestedBeans {

        @Test
        void loadsNestedBeans() {
            MemoryConfig config = new MemoryConfig();
            config.set("nested.str", "nestedValue");
            config.set("nested.num", 99);

            NestedBean bean = LOADER.load(NestedBean.class, config, LoadStrategy.JUST_LOAD);

            assertNotNull(bean.nested);
            assertEquals("nestedValue", bean.nested.str);
            assertEquals(99, bean.nested.num);
        }
    }

    @ConfigSource()
    public static class NestedBean {

        @Property("nested")
        public NestedValue nested;
    }

    public static class NestedValue {

        @Property("str")
        public String str;

        @Property("num")
        public int num;
    }

}
