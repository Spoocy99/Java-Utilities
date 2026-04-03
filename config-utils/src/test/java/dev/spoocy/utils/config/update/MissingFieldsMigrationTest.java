package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigProvider;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.migrations.MissingFieldsMigration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link MissingFieldsMigration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class MissingFieldsMigrationTest extends ConfigUpdaterTestBase {

	private static final VersionResolver DEFAULTS_VERSION_RESOLVER = pathVersionResolver("config-version", Version.ZERO);

	@Nested
	class Apply extends MissingFieldsContext {

		@Test
		void addsOnlyMissingLeafValues() {
			Config defaults = memoryConfig();
			defaults.set("config-version", "2.0.0");
			defaults.set("database.host", "db.local");
			defaults.set("database.port", 3306);
			defaults.set("feature.enabled", true);

			ConfigSection target = memoryConfig();
			target.set("database.host", "custom.host");

			boolean changed = migration(() -> defaults).apply(target);

			assertTrue(changed);
			assertEquals("custom.host", target.getString("database.host"));
			assertEquals(3306, target.getInt("database.port"));
			assertTrue(target.getBoolean("feature.enabled"));
		}

		@Test
		void skipsIncompatibleTreeMerges() {
			Config defaults = memoryConfig();
			defaults.set("database.host", "db.local");

			ConfigSection target = memoryConfig();
			target.set("database", "flat-value");

			assertTrue(migration(() -> defaults).apply(target));
			assertEquals("flat-value", target.getString("database"));
			assertFalse(target.isSet("database.host"));
		}

		@Test
		void wrapsProviderFailuresInIllegalStateException() {
			ConfigSection target = memoryConfig();

			MissingFieldsMigration migration = migration(() -> {
				throw new RuntimeException("boom");
			});

			IllegalStateException exception = assertThrows(IllegalStateException.class, () -> migration.apply(target));
			assertTrue(exception.getMessage().contains("Failed to apply defaults migration"));
			assertNotNull(exception.getCause());
		}
	}

	@Nested
	class ToVersion extends MissingFieldsContext {

		@Test
		void resolvesVersionFromDefaults() {
			Config defaults = memoryConfig();
			defaults.set("config-version", "1.5.0");

			Version version = migration(() -> defaults).toVersion();
			assertEquals("1.5.0", version.formatFull());
		}

		@Test
		void cachesResolvedVersionAndLoadedConfig() {
			AtomicInteger provideCalls = new AtomicInteger();
			AtomicInteger resolveCalls = new AtomicInteger();

			Config defaults = memoryConfig();
			defaults.set("config-version", "2.1.0");

			ConfigProvider provider = () -> {
				provideCalls.incrementAndGet();
				return defaults;
			};

			VersionResolver resolver = new VersionResolver() {
				@Override
				public Version resolve(ConfigSection config) {
					resolveCalls.incrementAndGet();
					return DEFAULTS_VERSION_RESOLVER.resolve(config);
				}

				@Override
				public void apply(ConfigSection config, Version version) {
					DEFAULTS_VERSION_RESOLVER.apply(config, version);
				}
			};

			MissingFieldsMigration migration = new MissingFieldsMigration(provider, VersionMatcher.ANY, resolver);
			Version first = migration.toVersion();
			Version second = migration.toVersion();

			assertEquals("2.1.0", first.formatFull());
			assertSame(first, second);
			assertEquals(1, provideCalls.get());
			assertEquals(1, resolveCalls.get());
		}
	}

	@Nested
	class Guards extends MissingFieldsContext {

		@Test
		void rejectsNullVersionResolver() {
			assertThrows(NullPointerException.class,
					() -> new MissingFieldsMigration(memoryConfig(), VersionMatcher.ANY, nullValue()));
		}

		@Test
		void rejectsNullTargetConfig() {
			MissingFieldsMigration migration = migration(memoryConfig());
			assertThrows(NullPointerException.class, () -> migration.apply(nullValue()));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T nullValue() {
		return (T) null;
	}

	private abstract static class MissingFieldsContext {

		protected MissingFieldsMigration migration(Config defaults) {
			return new MissingFieldsMigration(defaults, VersionMatcher.ANY, DEFAULTS_VERSION_RESOLVER);
		}

		protected MissingFieldsMigration migration(ConfigProvider provider) {
			return new MissingFieldsMigration(provider, VersionMatcher.ANY, DEFAULTS_VERSION_RESOLVER);
		}
	}



}

