package dev.spoocy.utils.config.update;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.migrations.ValidationMigration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ValidationMigration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ValidationMigrationTest extends ConfigUpdaterTestBase {

	@Nested
	class Apply extends ValidationContext {

		@Test
		void returnsFalseWhenNoValidatorsAreRegistered() {
			assertFalse(migration().apply(memoryConfig()));
		}

		@Test
		void returnsTrueWhenAtLeastOneValidatorExists() {
			ValidationMigration migration = migration().addValidator(config -> {
			});
			assertTrue(migration.apply(memoryConfig()));
		}

		@Test
		void stopsValidationWhenValidatorThrows() {
			AtomicInteger calls = new AtomicInteger();
			ValidationMigration migration = migration()
					.addValidator(config -> {
						calls.incrementAndGet();
						throw new IllegalStateException("invalid");
					})
					.addValidator(config -> calls.incrementAndGet());

			assertThrows(IllegalStateException.class, () -> migration.apply(memoryConfig()));
			assertEquals(1, calls.get());
		}
	}

	@Nested
	class ValidatePath extends ValidationContext {

		@Test
		void runsValidatorOnlyWhenPathExists() {
			ConfigSection config = memoryConfig();
			AtomicInteger calls = new AtomicInteger();

			ValidationMigration migration = migration().validatePath("feature.enabled", section -> calls.incrementAndGet());
			migration.apply(config);
			assertEquals(0, calls.get());

			config.set("feature.enabled", true);
			migration.apply(config);
			assertEquals(1, calls.get());
		}
	}

	@Nested
	class RequirePath extends ValidationContext {

		@Test
		void setsDefaultWhenPathIsMissing() {
			ConfigSection config = memoryConfig();
			assertTrue(migration().requirePath("database.port", 3306).apply(config));
			assertEquals(3306, config.getInt("database.port"));
		}

		@Test
		void doesNotOverwriteExistingValue() {
			ConfigSection config = memoryConfig();
			config.set("database.port", 25565);

			migration().requirePath("database.port", 3306).apply(config);
			assertEquals(25565, config.getInt("database.port"));
		}
	}

	@Nested
	class ValidateAllowed extends ValidationContext {

		@Test
		void acceptsAllowedValue() {
			ConfigSection config = memoryConfig();
			config.set("mode", "INFO");

			assertTrue(migration().validateAllowed("mode", "INFO", "WARN").apply(config));
		}

		@Test
		void ignoresMissingPath() {
			assertTrue(migration().validateAllowed("mode", "INFO", "WARN").apply(memoryConfig()));
		}

		@Test
		void rejectsDisallowedValue() {
			ConfigSection config = memoryConfig();
			config.set("mode", "DEBUG");

			IllegalStateException exception = assertThrows(IllegalStateException.class,
					() -> migration().validateAllowed("mode", "INFO", "WARN").apply(config));

			assertTrue(exception.getMessage().contains("must be one of the allowed values"));
		}

		@Test
		void rejectsEmptyAllowedValues() {
			assertThrows(IllegalArgumentException.class, () -> migration().validateAllowed("mode"));
		}
	}

	@Nested
	class Guards extends ValidationContext {

		@Test
		void rejectsNullArguments() {
			ValidationMigration migration = migration();

			assertThrows(NullPointerException.class, () -> migration.addValidator(nullValue()));
			assertThrows(NullPointerException.class, () -> migration.validatePath(nullValue(), section -> {
			}));
			assertThrows(NullPointerException.class, () -> migration.validatePath("path", nullValue()));
			assertThrows(NullPointerException.class, () -> migration.requirePath(nullValue(), "value"));
			assertThrows(NullPointerException.class, () -> migration.validateAllowed(nullValue(), "value"));
			assertThrows(NullPointerException.class, () -> migration.validateAllowed("path", nullValue()));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T nullValue() {
		return (T) null;
	}

	private abstract static class ValidationContext {

		protected ValidationMigration migration() {
			return new ValidationMigration(version("1.0.0"), version("1.1.0"));
		}
	}


}

