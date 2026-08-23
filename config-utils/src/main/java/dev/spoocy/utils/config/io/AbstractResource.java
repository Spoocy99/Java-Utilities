package dev.spoocy.utils.config.io;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractResource implements Resource {

    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();

        try {
            return url.toURI();
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid URI syntax: " + url, ex);
        }
    }

    @Override
    public long contentLength() throws IOException {
        try (InputStream is = getInputStream()) {
            long size = 0;
            byte[] buf = new byte[256];
            int read;

            while ((read = is.read(buf)) != -1) {
                size += read;
            }

            return size;
        }
    }


    @Override
    public long lastModified() throws IOException {
        File fileToCheck = getFileForLastModifiedCheck();

        long lastModified = fileToCheck.lastModified();

        if (lastModified == 0L && !fileToCheck.exists()) {
            throw new FileNotFoundException("Resource cannot be resolved: " + getDescription());
        }

        return lastModified;
    }

    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    protected abstract String getDescription();

    @Override
    public int hashCode() {
        return this.getDescription()
                .hashCode();
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
