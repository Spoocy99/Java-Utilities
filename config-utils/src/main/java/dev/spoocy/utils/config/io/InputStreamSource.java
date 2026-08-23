package dev.spoocy.utils.config.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for objects that are sources for an {@link InputStream}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface InputStreamSource {

    /**
     * Return an {@link InputStream} for the content of an underlying resource.
     * <p>
     * Every call will create a <i>fresh</i> stream.
     *
     * @return the input stream for the underlying resource
     *
     * @throws java.io.FileNotFoundException if the underlying resource does not exist
     * @throws IOException                   if the content stream could not be opened
     */
    @NotNull
    InputStream getInputStream() throws IOException;

}
