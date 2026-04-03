package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * File system based {@link Resource} implementation.
 */
public class FileSystemResource extends AbstractResource implements WriteableResource {

    @NotNull
    private final String path;

    @NotNull
    private final Path filePath;

    @Nullable
    private final File file;

    public FileSystemResource(@NotNull String path) {
        this.path = FileUtils.cleanPath(path);
        this.file = new File(path);
        this.filePath = this.file.toPath();
    }

    public FileSystemResource(@NotNull File file) {
        this.path = FileUtils.cleanPath(file.getPath());
        this.file = file;
        this.filePath = file.toPath();
    }

    public FileSystemResource(@NotNull Path filePath) {
        this.path = FileUtils.cleanPath(filePath.toString());
        this.file = null;
        this.filePath = filePath;
    }

    public FileSystemResource(@NotNull FileSystem fileSystem, @NotNull String path) {
        this.path = FileUtils.cleanPath(path);
        this.file = null;
        this.filePath = fileSystem.getPath(this.path).normalize();
    }

    @Override
    protected String getDescription() {
        return "File System Resource [" + this.path + "]";
    }

    @Override
    public boolean exists() {
        return (this.file != null ? this.file.exists() : Files.exists(this.filePath));
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public @Nullable String getFilename() {
        String filename = this.filePath.getFileName() != null ? this.filePath.getFileName()
                .toString() : null;
        return filename != null && !filename.isEmpty() ? filename : null;
    }

    @Override
    public File getFile() {
        return this.file != null ? this.file : this.filePath.toFile();
    }

    @Override
    public Path getPath() {
        return this.filePath;
    }

    @Override
    public boolean isReadable() {
        return this.file != null
                ? this.file.canRead() && !this.file.isDirectory()
                : Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath);
    }

    @Override
    public boolean isWritable() {
        return this.file != null
                ? this.file.canWrite() && !this.file.isDirectory()
                : Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath);
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        // Directories cannot be opened as input streams on many platforms (Windows throws
        // AccessDeniedException). Match test expectations by reporting a FileNotFoundException
        // for directory resources instead of letting platform-specific IOExceptions escape.
        if (Files.isDirectory(this.filePath)) {
            throw new FileNotFoundException(getDescription() + " is a directory");
        }

        try {
            return FileUtils.createInputStream(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public long contentLength() throws IOException {
        // For file system resources prefer to return the file size directly instead of
        // opening an InputStream. This avoids platform-specific errors when paths point
        // to directories (Windows does not allow opening a directory as a stream).
        if (this.file != null) {
            if (!this.file.exists()) {
                throw new FileNotFoundException("Resource cannot be resolved: " + getDescription());
            }

            // Return length for files, and for directories File.length() is the expected
            // value used by tests (typically 0).
            return this.file.length();
        }

        if (Files.notExists(this.filePath)) {
            throw new FileNotFoundException("Resource cannot be resolved: " + getDescription());
        }

        if (Files.isDirectory(this.filePath)) {
            // Directory size: tests expect the directory's file length (usually 0). We cannot
            // use Files.size on directories reliably on all platforms, so return 0L.
            return 0L;
        }

        try {
            return Files.size(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public Resource createRelative(@NotNull String relativePath) {
        String pathToUse = FileUtils.applyRelativePath(this.path, relativePath);
        return (this.file != null ? new FileSystemResource(pathToUse) :
                new FileSystemResource(this.filePath.getFileSystem(), pathToUse));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (Files.isDirectory(this.filePath)) {
            throw new FileNotFoundException(getDescription() + " is a directory");
        }

        try {
            return FileUtils.createOutputStream(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
}
