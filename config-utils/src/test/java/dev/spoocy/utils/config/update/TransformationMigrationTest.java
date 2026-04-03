package dev.spoocy.utils.config.update;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.migrations.TransformationMigration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TransformationMigration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class TransformationMigrationTest extends ConfigUpdaterTestBase {

	@Nested
	class RenameKey extends TransformationContext {

		@Test
		void renamesWhenOldPathExistsAndNewPathIsMissing() {
			ConfigSection config = memoryConfig();
			config.set("old.path", "value");

			assertTrue(migration().renameKey("old.path", "new.path").apply(config));
			assertFalse(config.isSet("old.path"));
			assertEquals("value", config.getString("new.path"));
		}

		@Test
		void skipsWhenOldPathDoesNotExist() {
			ConfigSection config = memoryConfig();

			assertFalse(migration().renameKey("missing", "target").apply(config));
			assertFalse(config.isSet("target"));
		}

		@Test
		void skipsWhenNewPathAlreadyExists() {
			ConfigSection config = memoryConfig();
			config.set("old.path", "old");
			config.set("new.path", "new");

			assertFalse(migration().renameKey("old.path", "new.path").apply(config));
			assertEquals("old", config.getString("old.path"));
			assertEquals("new", config.getString("new.path"));
		}
	}

	@Nested
	class RemoveKey extends TransformationContext {

		@Test
		void removesExistingPath() {
			ConfigSection config = memoryConfig();
			config.set("legacy", true);

			assertTrue(migration().removeKey("legacy").apply(config));
			assertFalse(config.isSet("legacy"));
		}

		@Test
		void skipsMissingPath() {
			assertFalse(migration().removeKey("missing").apply(memoryConfig()));
		}
	}

	@Nested
	class TransformValue extends TransformationContext {

		@Test
		void transformsExistingValue() {
			ConfigSection config = memoryConfig();
			config.set("threads", "4");

			assertTrue(migration().transformValue("threads", value -> Integer.parseInt(value.toString()) * 2).apply(config));
			assertEquals(8, config.getInt("threads"));
		}

		@Test
		void skipsMissingPath() {
			assertFalse(migration().transformValue("missing", value -> value).apply(memoryConfig()));
		}

		@Test
		void wrapsTransformerExceptions() {
			ConfigSection config = memoryConfig();
			config.set("value", "x");

			IllegalStateException exception = assertThrows(IllegalStateException.class,
					() -> migration().transformValue("value", value -> {
						throw new IllegalArgumentException("boom");
					}).apply(config));

			assertTrue(exception.getMessage().contains("Failed to transform value at path 'value'"));
			assertNotNull(exception.getCause());
		}
	}

	@Nested
	class CustomTransformations extends TransformationContext {

		@Test
		void appliesCustomTransformationsInOrder() {
			ConfigSection config = memoryConfig();
			List<String> order = new ArrayList<>();

			TransformationMigration migration = migration()
					.addTransformation(section -> {
						order.add("first");
						section.set("value", "A");
						return true;
					})
					.addTransformation(section -> {
						order.add("second");
						section.set("value", section.getString("value") + "B");
						return true;
					});

			assertTrue(migration.apply(config));
			assertEquals(List.of("first", "second"), order);
			assertEquals("AB", config.getString("value"));
		}

		@Test
		void returnsFalseWhenNoTransformationsApply() {
			assertFalse(migration().apply(memoryConfig()));
		}

		@Test
		void stopsExecutionWhenTransformationThrows() {
			AtomicInteger calls = new AtomicInteger();
			TransformationMigration migration = migration()
					.addTransformation(section -> {
						calls.incrementAndGet();
						throw new IllegalStateException("fail");
					})
					.addTransformation(section -> {
						calls.incrementAndGet();
						return true;
					});

			assertThrows(IllegalStateException.class, () -> migration.apply(memoryConfig()));
			assertEquals(1, calls.get());
		}
	}

	@Nested
	class Guards extends TransformationContext {

		@Test
		void rejectsNullArguments() {
			TransformationMigration migration = migration();

			assertThrows(NullPointerException.class, () -> migration.renameKey(nullValue(), "target"));
			assertThrows(NullPointerException.class, () -> migration.renameKey("source", nullValue()));
			assertThrows(NullPointerException.class, () -> migration.removeKey(nullValue()));
			assertThrows(NullPointerException.class, () -> migration.transformValue(nullValue(), value -> value));
			assertThrows(NullPointerException.class, () -> migration.transformValue("path", nullValue()));
			assertThrows(NullPointerException.class, () -> migration.addTransformation(nullValue()));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T nullValue() {
		return (T) null;
	}

	private abstract static class TransformationContext {

		protected TransformationMigration migration() {
			return new TransformationMigration(version("1.0.0"), version("2.0.0"));
		}
	}


}

