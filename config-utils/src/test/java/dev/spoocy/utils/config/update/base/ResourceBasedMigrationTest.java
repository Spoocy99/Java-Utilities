package dev.spoocy.utils.config.update.base;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigProvider;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.ConfigUpdaterTestBase;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ResourceBasedMigration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class ResourceBasedMigrationTest extends ConfigUpdaterTestBase {

    @Nested
    class ResourceLoading {

        @Test
        void returnsConfiguredConfigProvider() {
            ConfigProvider provider = ResourceBasedMigrationTest::memoryConfig;
            TestResourceMigration migration = new TestResourceMigration(provider);

            assertSame(provider, migration.getConfigProvider());
        }

        @Test
        void cachesLoadedResourceAfterFirstCall() {
            AtomicInteger provideCalls = new AtomicInteger();
            Config defaults = memoryConfig();
            ConfigProvider provider = () -> {
                provideCalls.incrementAndGet();
                return defaults;
            };

            TestResourceMigration migration = new TestResourceMigration(provider);

            Config first = migration.exposedLoadResource();
            Config second = migration.exposedLoadResource();

            assertSame(first, second);
            assertEquals(1, provideCalls.get());
        }

        @Test
        void wrapsProviderFailuresInIllegalStateException() {
            TestResourceMigration migration = new TestResourceMigration(() -> {
                throw new RuntimeException("broken");
            });

            IllegalStateException exception = assertThrows(IllegalStateException.class, migration::exposedLoadResource);
            assertNotNull(exception.getCause());
            assertTrue(exception.getMessage().contains("Failed to load configuration resource for migration"));
        }
    }

    private static final class TestResourceMigration extends ResourceBasedMigration {

        private TestResourceMigration(ConfigProvider configProvider) {
            super(VersionMatcher.ANY, Version.parse("1.0.0"), configProvider);
        }

        @Override
        public boolean apply(@NotNull ConfigSection config) {
            return false;
        }

        private Config exposedLoadResource() {
            return loadResource();
        }
    }

}


