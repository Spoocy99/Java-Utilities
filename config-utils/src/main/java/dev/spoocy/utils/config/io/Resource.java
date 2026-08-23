package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Resource extends InputStreamSource {

    /**
     * Determine whether this resource actually exists in physical form.
     *
     * @return {@code true} if the resource exists, {@code false} otherwise
     */
    boolean exists();

    /**
     * @return a URL handle for this resource.
     *
     * @throws IOException if the resource cannot be resolved as URL
     */
    URL getURL() throws IOException;

    /**
     * @return a URI handle for this resource.
     *
     * @throws IOException if the resource cannot be resolved as URI
     */
    URI getURI() throws IOException;

    /**
     * @return {@code true} if the resource represents a file, {@code false} otherwise
     */
    boolean isFile();

    /**
     * @return a filename for this resource
     */
    @Nullable
    String getFilename();

    /**
     * Return a File handle for this resource.
     * <p>
     * This only works for files in the default file system.
     *
     * @throws UnsupportedOperationException if the resource is a file but cannot be exposed as a File
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as a file
     * @throws IOException                   in case of general resolution/reading failures
     */
    File getFile() throws IOException;

    /**
     * @return an IO Path handle for this resource.
     *
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as a file
     * @throws IOException                   in case of general resolution/reading failures
     */
    Path getPath() throws IOException;

    /**
     * Indicate whether this resource is readable.
     *
     * @return {@code true} if the resource is readable, {@code false} otherwise
     */
    boolean isReadable();

    /**
     * Return an {@link InputStream} for reading the underlying resource.
     *
     * @return the InputStream to read from
     *
     * @throws IOException if the stream could not be opened
     * @see #isReadable()
     */
    @Override
    @NotNull InputStream getInputStream() throws IOException;

    /**
     * @return the contents of this resource as byte array
     *
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as a file
     * @throws IOException                   in case of general resolution/reading failures
     */
    default byte[] getContentAsByteArray() throws IOException {
        return FileUtils.copyToByteArray(getInputStream());
    }

    /**
     * Return the contents of this resource as a string, using the specified charset.
     *
     * @param charset the charset to use for decoding
     *
     * @return the contents of this resource as a {@code String}
     *
     * @throws java.io.FileNotFoundException if the resource cannot be resolved as
     */
    default String getContentAsString(@NotNull Charset charset) throws IOException {
        return FileUtils.copyToString(new InputStreamReader(getInputStream(), charset));
    }

    /**
     * Determine the content length for this resource.
     *
     * @return the content length (or -1 if undetermined)
     *
     * @throws IOException if the resource cannot be resolved
     */
    long contentLength() throws IOException;

    /**
     * Determine the last-modified timestamp for this resource.
     *
     * @return the last-modified timestamp (or 0 if not known)
     *
     * @throws IOException if the resource cannot be resolved
     */
    long lastModified() throws IOException;

    /**
     * Create a resource relative to this resource.
     *
     * @param relativePath the relative path (relative to this resource)
     *
     * @return the resource handle for the relative resource
     *
     * @throws IOException if the relative resource cannot be determined
     */
    Resource createRelative(@NotNull String relativePath) throws IOException;

}
