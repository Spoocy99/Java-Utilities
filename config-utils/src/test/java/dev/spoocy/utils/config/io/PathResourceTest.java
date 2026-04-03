package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.Resources;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class PathResourceTest extends ResourceTest {

    private static final String EXISTING_DIR =
            resourcesPath("io");

    private static final String EXISTING_FILE =
            resourcesPath("io/example.properties");

    private static final String NON_EXISTING_FILE =
            resourcesPath("io/definitely-does-not-exist.properties");

    @Nested
    class Creation {

        @Test
        void createFromPath() throws IOException {
            Path path = Paths.get(EXISTING_FILE);
            PathResource resource = Resources.fromPath(path);
            assertEquals(
                    path.toString(),
                    resource.getPath().toString()
            );
        }

        @Test
        void createFromString() throws IOException {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            assertEquals(
                    EXISTING_FILE,
                    resource.getPath().toString()
            );
        }

        @Test
        void createFromUri() throws IOException {
            File file = new File(EXISTING_FILE);
            PathResource resource = Resources.fromPath(file.toURI());
            assertEquals(
                    file.getAbsoluteFile().toString(),
                    resource.getPath().toString()
            );
        }

    }

    @Nested
    class Dir {

        @Test
        void exists() {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            assertTrue(resource.exists());
        }

        @Test
        void readable() {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            assertFalse(resource.isReadable());
        }

        @Test
        void inputStream() {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            assertThrows(FileNotFoundException.class, resource::getInputStream);
        }

        @Test
        void contentLength() throws IOException {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            File file = new File(EXISTING_DIR);
            assertEquals(file.length(), resource.contentLength());
        }

        @Test
        void writeable() {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            assertFalse(resource.isWritable());
        }

        @Test
        void outputStream() {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            assertThrows(FileNotFoundException.class, resource::getOutputStream);
        }

    }

    @Nested
    class Existing {

        @Test
        void exists() {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            assertTrue(resource.exists());
        }

        @Test
        void readable() {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            assertTrue(resource.isReadable());
        }

        @Test
        void inputStream() throws IOException {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            byte[] bytes = FileUtils.copyToByteArray(resource.getInputStream());
            assertTrue(bytes.length > 0);
        }

        @Test
        void contentLength() throws IOException {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            File file = new File(EXISTING_FILE);
            assertEquals(file.length(), resource.contentLength());
        }

        @Test
        void lastModified() throws IOException {
            PathResource resource = Resources.fromPath(EXISTING_DIR);
            File file = new File(EXISTING_DIR);
            assertEquals(file.lastModified() / 1000, resource.lastModified() / 1000);
        }

        @Test
        void writeable() {
            PathResource resource = Resources.fromPath(EXISTING_FILE);
            assertTrue(resource.isWritable());
        }

        @Test
        void outputStream(@TempDir Path temporaryFolder) throws IOException {
            PathResource resource = new PathResource(temporaryFolder.resolve("test"));
            FileUtils.copy("test".getBytes(StandardCharsets.UTF_8), resource.getOutputStream());
            assertEquals(4L, resource.contentLength());
        }

    }

    @Nested
    class NonExisting {

        @Test
        void exists() {
            PathResource resource = Resources.fromPath(NON_EXISTING_FILE);
            assertFalse(resource.exists());
        }

        @Test
        void readable() {
            PathResource resource = Resources.fromPath(NON_EXISTING_FILE);
            assertFalse(resource.isReadable());
        }

        @Test
        void inputStream() {
            PathResource resource = Resources.fromPath(NON_EXISTING_FILE);
            assertThrows(FileNotFoundException.class, resource::getInputStream);
        }

        @Test
        void contentLength() throws IOException {
            PathResource resource = Resources.fromPath(NON_EXISTING_FILE);
            assertThrows(NoSuchFileException.class, resource::contentLength);
        }

        @Test
        void writeable() {
            PathResource resource = Resources.fromPath(NON_EXISTING_FILE);
            assertFalse(resource.isWritable());
        }

        @Test
        void outputStream(@TempDir Path temporaryFolder) throws IOException {
            File file = temporaryFolder.resolve("test").toFile();
            file.delete();

            PathResource resource = new PathResource(file.toPath());
            FileUtils.copy("test".getBytes(), resource.getOutputStream());
            assertEquals(4L, resource.contentLength());
        }

    }


}
