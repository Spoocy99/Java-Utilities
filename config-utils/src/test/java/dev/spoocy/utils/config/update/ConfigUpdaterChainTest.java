package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ConfigUpdaterChain}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
class ConfigUpdaterChainTest extends ConfigUpdaterTestBase {

	@Nested
	class Builder {

		@Test
		void buildWithoutMigrationsFails() {
			assertThrows(IllegalStateException.class, () -> ConfigUpdater.chain().build());
		}

		@Test
		void buildDefaultsTargetToHighestMigrationVersion() {
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), true))
					.apply(new TrackingMigration(VersionMatcher.exact(version("1.1.0")), version("2.0.0"), true))
					.build();

			assertEquals("2.0.0", chain.getTargetVersion().formatFull());
		}

		@Test
		void buildRejectsDuplicateMatchers() {
			TrackingMigration first = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), true);
			TrackingMigration second = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.2.0"), true);

			assertThrows(IllegalArgumentException.class, () -> ConfigUpdater.chain()
					.apply(VersionMatcher.exact(version("1.0.0")), first)
					.apply(VersionMatcher.exact(version("1.0.0")), second)
					.build());
		}

		@Test
		void getPossibleMigrationsIsUnmodifiable() {
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(new TrackingMigration(VersionMatcher.ANY, version("1.0.0"), true))
					.targetVersion(version("1.0.0"))
					.build();

			Collection<ConfigMigration> possible = chain.getPossibleMigrations();
			assertThrows(UnsupportedOperationException.class, possible::clear);
		}
	}

	@Nested
	class Run {

		@Test
		void runsSequentialMigrationsUntilTargetVersion() {
			TrackingMigration first = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), true);
			TrackingMigration second = new TrackingMigration(VersionMatcher.exact(version("1.1.0")), version("1.2.0"), true);

			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(first)
					.apply(second)
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertEquals(2, chain.run(config));
			assertEquals("1.2.0", config.getString("config-version"));
			assertEquals(1, first.applyCalls.get());
			assertEquals(1, second.applyCalls.get());
		}

		@Test
		void prefersExactMatcherBeforeNonExactMatcher() {
			TrackingMigration exact = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), true);
			TrackingMigration any = new TrackingMigration(VersionMatcher.ANY, version("5.0.0"), true);

			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(any)
					.apply(exact)
					.targetVersion(version("1.1.0"))
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertEquals(1, chain.run(config));
			assertEquals(1, exact.applyCalls.get());
			assertEquals(0, any.applyCalls.get());
			assertEquals("1.1.0", config.getString("config-version"));
		}

		@Test
		void stopsWhenNoMigrationMatchesCurrentVersion() {
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(new TrackingMigration(VersionMatcher.exact(version("2.0.0")), version("2.1.0"), true))
					.targetVersion(version("3.0.0"))
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertEquals(0, chain.run(config));
			assertEquals("1.0.0", config.getString("config-version"));
		}

		@Test
		void stopsWhenMigrationReturnsFalse() {
			TrackingMigration migration = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), false);
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(migration)
					.targetVersion(version("2.0.0"))
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertEquals(0, chain.run(config));
			assertEquals(1, migration.applyCalls.get());
			assertEquals("1.0.0", config.getString("config-version"));
		}

		@Test
		void stopsWhenMigrationDoesNotAdvanceVersion() {
			TrackingMigration stagnant = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.0.0"), true);
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(stagnant)
					.targetVersion(version("3.0.0"))
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertEquals(1, chain.run(config));
			assertEquals(1, stagnant.applyCalls.get());
			assertEquals("1.0.0", config.getString("config-version"));
		}

		@Test
		void usesVersionPathFallbackWhenVersionIsMissing() {
			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.versionPath("schema-version", version("0.5.0"))
					.apply(new TrackingMigration(VersionMatcher.exact(version("0.5.0")), version("1.0.0"), true))
					.targetVersion(version("1.0.0"))
					.build();

			ConfigSection config = memoryConfig();

			assertEquals(1, chain.run(config));
			assertEquals("1.0.0", config.getString("schema-version"));
		}

		@Test
		void throwsWhenAppliedMigrationHasNullTargetVersion() {
			ConfigMigration invalidMigration = mock(ConfigMigration.class);
			when(invalidMigration.fromVersion()).thenReturn(VersionMatcher.exact(version("1.0.0")));
			when(invalidMigration.apply(any(ConfigSection.class))).thenReturn(true);
			when(invalidMigration.toVersion()).thenReturn(null);

			ConfigUpdaterChain chain = ConfigUpdater.chain()
					.apply(invalidMigration)
					.targetVersion(version("2.0.0"))
					.build();

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");

			assertThrows(NullPointerException.class, () -> chain.run(config));
		}
	}

	@Nested
	class Constructor {

		@Test
		void constructorUsesMigrationMatcherWhenExplicitMatcherIsNull() {
			TrackingMigration migration = new TrackingMigration(VersionMatcher.exact(version("1.0.0")), version("1.1.0"), true);

			Map<ConfigMigration, VersionMatcher> migrations = new LinkedHashMap<>();
			migrations.put(migration, null);

			ConfigUpdaterChain chain = new ConfigUpdaterChain(
					pathVersionResolver("config-version", version("0.0.0")),
					migrations,
					version("1.1.0")
			);

			ConfigSection config = memoryConfig();
			config.set("config-version", "1.0.0");
			assertEquals(1, chain.run(config));
		}
	}

	private static final class TrackingMigration implements ConfigMigration {

		private final VersionMatcher matcher;
		private final Version toVersion;
		private final boolean applyResult;
		private final AtomicInteger applyCalls = new AtomicInteger();

		private TrackingMigration(@NotNull VersionMatcher matcher, @NotNull Version toVersion, boolean applyResult) {
			this.matcher = matcher;
			this.toVersion = toVersion;
			this.applyResult = applyResult;
		}

		@Override
		public VersionMatcher fromVersion() {
			return this.matcher;
		}

		@Override
		public @NotNull Version toVersion() {
			return this.toVersion;
		}

		@Override
		public boolean apply(@NotNull ConfigSection config) {
			this.applyCalls.incrementAndGet();
			return this.applyResult;
		}
	}


}

