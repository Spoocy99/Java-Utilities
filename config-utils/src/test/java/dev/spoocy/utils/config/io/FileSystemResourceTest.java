package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class FileSystemResourceTest extends ResourceTest {

	private static final String EXISTING_DIR = resourcesPath("io");
	private static final String EXISTING_FILE = resourcesPath("io/example.properties");
	private static final String NON_EXISTING_FILE = resourcesPath("io/does-not-exist.properties");

	@Nested
	class Dir {

		@Test
		void exists() {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			assertTrue(resource.exists());
		}

		@Test
		void readable() {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			assertFalse(resource.isReadable());
		}

		@Test
		void inputStream() {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			assertThrows(FileNotFoundException.class, resource::getInputStream);
		}

		@Test
		void contentLength() throws IOException {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			File file = new File(EXISTING_DIR);
			assertEquals(file.length(), resource.contentLength());
		}

		@Test
		void writeable() {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			assertFalse(resource.isWritable());
		}

		@Test
		void outputStream() {
			FileSystemResource resource = new FileSystemResource(EXISTING_DIR);
			assertThrows(FileNotFoundException.class, resource::getOutputStream);
		}

	}

	@Nested
	class Existing {

		@Test
		void exists() {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			assertTrue(resource.exists());
		}

		@Test
		void readable() {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			assertTrue(resource.isReadable());
		}

		@Test
		void inputStream() throws IOException {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			byte[] bytes = FileUtils.copyToByteArray(resource.getInputStream());
			assertTrue(bytes.length > 0);
		}

		@Test
		void contentLength() throws IOException {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			File file = new File(EXISTING_FILE);
			assertEquals(file.length(), resource.contentLength());
		}

		@Test
		void lastModified() throws IOException {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			File file = new File(EXISTING_FILE);
			assertEquals(file.lastModified() / 1000, resource.lastModified() / 1000);
		}

		@Test
		void writeable() {
			FileSystemResource resource = new FileSystemResource(EXISTING_FILE);
			assertTrue(resource.isWritable());
		}

		@Test
		void outputStream(@TempDir Path temporaryFolder) throws IOException {
			FileSystemResource resource = new FileSystemResource(temporaryFolder.resolve("test"));
			FileUtils.copy("test".getBytes(StandardCharsets.UTF_8), resource.getOutputStream());
			assertEquals(4L, resource.contentLength());
		}

	}

	@Nested
	class NonExisting {

		@Test
		void exists() {
			FileSystemResource resource = new FileSystemResource(NON_EXISTING_FILE);
			assertFalse(resource.exists());
		}

		@Test
		void readable() {
			FileSystemResource resource = new FileSystemResource(NON_EXISTING_FILE);
			assertFalse(resource.isReadable());
		}

		@Test
		void inputStream() {
			FileSystemResource resource = new FileSystemResource(NON_EXISTING_FILE);
			assertThrows(FileNotFoundException.class, resource::getInputStream);
		}

		@Test
		void contentLength() throws IOException {
			FileSystemResource resource = new FileSystemResource(NON_EXISTING_FILE);
			assertThrows(FileNotFoundException.class, resource::contentLength);
		}

		@Test
		void writeable() {
			FileSystemResource resource = new FileSystemResource(NON_EXISTING_FILE);
			assertFalse(resource.isWritable());
		}

		@Test
		void outputStream(@TempDir Path temporaryFolder) throws IOException {
			File file = temporaryFolder.resolve("test").toFile();
			file.delete();

			FileSystemResource resource = new FileSystemResource(file.toPath());
			FileUtils.copy("test".getBytes(), resource.getOutputStream());
			assertEquals(4L, resource.contentLength());
		}

	}

}

