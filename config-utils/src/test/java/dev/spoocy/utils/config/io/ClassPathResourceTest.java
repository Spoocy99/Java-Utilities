package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassPathResourceTest extends ResourceTest {

	private static final String EXISTING_RESOURCE = "example.properties";
	private static final String EXISTING_RESOURCE_CLASSPATH = "dev/spoocy/utils/config/io/example.properties";
	private static final String NON_EXISTING_RESOURCE = "does-not-exist.properties";

	@Nested
	class Creation {

		@Test
		void createFromClass() throws IOException {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE, ClassPathResourceTest.class);
			String expected = ClassPathResourceTest.class.getPackage().getName().replace('.', '/') + "/" + EXISTING_RESOURCE;
			// Path.of will normalize separators for the current OS, so compare using that representation
			assertEquals(Path.of(expected).toString(), resource.getPath().toString());
		}

		@Test
		void createFromClassLoader() throws IOException {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			assertEquals(Path.of(EXISTING_RESOURCE_CLASSPATH).toString(), resource.getPath().toString());
		}

	}

	@Nested
	class Existing {

		@Test
		void exists() {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			assertTrue(resource.exists());
		}

		@Test
		void readable() {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			assertTrue(resource.isReadable());
		}

		@Test
		void inputStream() throws IOException {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			byte[] bytes = FileUtils.copyToByteArray(resource.getInputStream());
			assertTrue(bytes.length > 0);
		}

		@Test
		void filename() {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			assertEquals(EXISTING_RESOURCE, resource.getFilename());
		}

		@Test
		void urlContainsResourceName() throws IOException {
			ClassPathResource resource = new ClassPathResource(EXISTING_RESOURCE_CLASSPATH, ClassPathResourceTest.class.getClassLoader());
			URL url = resource.getURL();
			assertNotNull(url);
			assertTrue(url.toString().contains(EXISTING_RESOURCE));
		}

	}

	@Nested
	class NonExisting {

		@Test
		void exists() {
			ClassPathResource resource = new ClassPathResource(NON_EXISTING_RESOURCE, ClassPathResourceTest.class);
			assertFalse(resource.exists());
		}

		@Test
		void readable() {
			ClassPathResource resource = new ClassPathResource(NON_EXISTING_RESOURCE, ClassPathResourceTest.class);
			assertFalse(resource.isReadable());
		}

		@Test
		void inputStream() {
			ClassPathResource resource = new ClassPathResource(NON_EXISTING_RESOURCE, ClassPathResourceTest.class);
			assertThrows(FileNotFoundException.class, resource::getInputStream);
		}

		@Test
		void filename() {
			ClassPathResource resource = new ClassPathResource(NON_EXISTING_RESOURCE, ClassPathResourceTest.class);
			assertEquals(NON_EXISTING_RESOURCE, resource.getFilename());
		}

	}

}

