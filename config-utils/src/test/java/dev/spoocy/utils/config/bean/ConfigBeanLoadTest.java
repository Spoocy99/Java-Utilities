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

    /**
     * Errors
     */
    @Nested
    class Errors {

        @Test
        void rejectsClassesWithoutConfigSource() {
            MemoryConfig config = new MemoryConfig();
            assertThrows(IllegalArgumentException.class, () -> LOADER.load(UnannotatedBean.class, config, LoadStrategy.JUST_LOAD));
        }

    }

    public static class UnannotatedBean {
        public String value1;
        public static String value2;
    }

    /**
     * Behaviour Tests
     */
    @Nested
    class Behaviour {

        @Test
        void ignoreFinalFields() {
            MemoryConfig config = new MemoryConfig();
            config.set("value", "test123");
            config.set("value2", "test123");

            FinalValueBean bean = new FinalValueBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            assertEquals("test", bean.value1);
            assertEquals("test", FinalValueBean.value2);
        }

        @Test
        void ignoreTransientFields() {
            MemoryConfig config = new MemoryConfig();
            config.set("value1", "test123");
            config.set("value2", "test123");

            TransientValueBean bean = new TransientValueBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            assertEquals("test", bean.value1);
            assertEquals("test", TransientValueBean.value2);
        }

        @Test
        void ignoreAnnotatedFields() {
            MemoryConfig config = new MemoryConfig();
            config.set("value1", "test123");
            config.set("value2", "test123");

            TransientAnnotatedValueBean bean = new TransientAnnotatedValueBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            assertEquals("test", bean.value1);
            assertEquals("test", TransientAnnotatedValueBean.value2);
        }

        @Test
        void loadOnlyStaticsOnClassProvided() {
            MemoryConfig config = new MemoryConfig();
            config.set("value1", "test123");
            config.set("value2", "test123");

            MixedBean1 bean = new MixedBean1();
            // load via static
            LOADER.loadStatic(MixedBean1.class, config, LoadStrategy.JUST_LOAD);

            assertEquals("test", bean.value1);
            assertEquals("test123", MixedBean1.value2);
        }

        @Test
        void loadAllOnInstanceProvided() {
            MemoryConfig config = new MemoryConfig();
            config.set("value1", "test123");
            config.set("value2", "test123");

            MixedBean2 bean = new MixedBean2();
            // load via instance
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            assertEquals("test123", bean.value1);
            assertEquals("test123", MixedBean2.value2);
        }

    }

    @ConfigSource()
    public static class FinalValueBean {
        public final String value1 = "test";
        public static final String value2 = "test";
    }

    @ConfigSource()
    public static class TransientValueBean {
        public transient String value1 = "test";
        public static transient String value2 = "test";
    }

    @ConfigSource()
    public static class TransientAnnotatedValueBean {
        @Transient public String value1 = "test";
        @Transient public static String value2 = "test";
    }

    @ConfigSource()
    public static class MixedBean1 {
        public String value1 = "test";
        public static String value2 = "test";
    }

    @ConfigSource()
    public static class MixedBean2 {
        public String value1 = "test";
        public static String value2 = "test";
    }

    /**
     * Primitive Data types
     */
    @Nested
    class Primitives {

        @Test
        void loadsPrimitives() {
            MemoryConfig config = new MemoryConfig();
            config.set("str", "test123");
            config.set("num", 12);

            PrimitivesBean bean = new PrimitivesBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            // overwritten by config
            assertEquals("test123", bean.str);
            assertEquals(12, bean.num);

            // default value
            assertFalse(bean.bool);
        }

        @Test
        void loadsStaticPrimitives() {
            MemoryConfig config = new MemoryConfig();
            config.set("str", "test123");
            config.set("num", 12);

            LOADER.loadStatic(StaticPrimitivesBean.class, config, LoadStrategy.JUST_LOAD);

            // overwritten by config
            assertEquals("test123", StaticPrimitivesBean.str);
            assertEquals(12, StaticPrimitivesBean.num);

            // default value
            assertFalse(StaticPrimitivesBean.bool);
        }

        @Test
        void loadsMixedPrimitives() {
            MemoryConfig config = new MemoryConfig();
            config.set("str", "test123");
            config.set("num", 12);

            MixedPrimitivesBean bean = new MixedPrimitivesBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

            // overwritten by config
            assertEquals("test123", bean.str);
            assertEquals(12, MixedPrimitivesBean.num);
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

    @ConfigSource()
    public static class StaticPrimitivesBean {

        @Property("str")
        public static String str = "test";

        @Property("num")
        public static int num = 1;

        @Property("bool")
        public static boolean bool = false;
    }

    @ConfigSource()
    public static class MixedPrimitivesBean {

        @Property("str")
        public String str = "test";

        @Property("num")
        public static int num = 1;

    }

    /**
     * Collections Data types
     */
    @Nested
    class Collections {

        @Test
        void loadsCollections() {
            MemoryConfig config = new MemoryConfig();
            config.set("list", List.of("a", "b", "c"));
            config.set("map", Map.of("key1", "value1", "key2", 42));

            CollectionsBean bean = new CollectionsBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

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

    /**
     * Nested Data types
     */
    @Nested
    class NestedBeans {

        @Test
        void loadsNestedBeans() {
            MemoryConfig config = new MemoryConfig();
            config.set("nested.str", "nestedValue");
            config.set("nested.num", 99);

            NestedBean bean = new NestedBean();
            LOADER.load(bean, config, LoadStrategy.JUST_LOAD);

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
