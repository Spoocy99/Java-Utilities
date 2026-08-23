package dev.spoocy.utils.config.update.base;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.ConfigUpdaterTestBase;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BaseMigration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class BaseMigrationTest extends ConfigUpdaterTestBase {

    @Nested
    class FromVersion {

        @Test
        void defaultsToAnyMatcherWhenNullMatcherIsProvided() {
            TestMigration migration = new TestMigration((VersionMatcher) null, version("1.1.0"));
            assertSame(VersionMatcher.ANY, migration.fromVersion());
        }

        @Test
        void wrapsExplicitFromVersionInExactMatcher() {
            TestMigration migration = new TestMigration(version("1.0.0"), version("1.1.0"));
            VersionMatcher matcher = migration.fromVersion();

            assertNotNull(matcher);
            assertTrue(matcher.matches(version("1.0.0")));
            assertFalse(matcher.matches(version("1.0.1")));
            assertTrue(matcher.isExact());
        }
    }

    @Nested
    class ToVersion {

        @Test
        void returnsConstructorTargetVersion() {
            TestMigration migration = new TestMigration(VersionMatcher.ANY, version("2.0.0"));
            assertEquals("2.0.0", migration.toVersion().formatFull());
        }

        @Test
        void throwsWhenNoTargetVersionIsAvailable() {
            TestMigration migration = new TestMigration(VersionMatcher.ANY, null);
            assertThrows(IllegalStateException.class, migration::toVersion);
        }

        @Test
        void supportsLazyVersionResolutionWhenOverridden() {
            LazyVersionMigration migration = new LazyVersionMigration();
            assertEquals("3.0.0", migration.toVersion().formatFull());
        }
    }

    private static class TestMigration extends BaseMigration {

        private TestMigration(VersionMatcher matcher, Version toVersion) {
            super(matcher, toVersion);
        }

        private TestMigration(Version fromVersion, Version toVersion) {
            super(fromVersion, toVersion);
        }

        @Override
        public boolean apply(@NotNull ConfigSection config) {
            return false;
        }
    }

    private static final class LazyVersionMigration extends TestMigration {

        private LazyVersionMigration() {
            super(VersionMatcher.ANY, null);
        }

        @Override
        public @NotNull Version toVersion() {
            return version("3.0.0");
        }
    }
}

