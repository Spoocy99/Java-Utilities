package dev.spoocy.utils.config.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface WriteableResource extends Resource {

    /**
     * Indicate whether the contents of this resource can be written via {@link #getOutputStream()}.
     *
     * @return {@code true} if the contents of this resource can be written, {@code false} otherwise
     */
    boolean isWritable();

    /**
     * Return an {@link OutputStream} for writing to the underlying resource.
     *
     * @return the OutputStream to write to
     *
     * @throws IOException                   if the stream could not be opened
     * @throws UnsupportedOperationException if the resource cannot be written to (i.e. is not {@link #isWritable() writable})
     *
     * @see #isWritable()
     */
    OutputStream getOutputStream() throws IOException;

}
