package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ResolvableResource extends AbstractResource {

    @Override
    public boolean exists() {
        try {
            URL url = getURL();

            if (FileUtils.isFileURL(url)) {
                // Proceed with file system resolution
                return getFile().exists();
            }

            // Try a URL connection content-length header
            URLConnection con = url.openConnection();
            applyCaches(con);

            HttpURLConnection httpCon = (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);

            if (httpCon != null) {
                httpCon.setRequestMethod("HEAD");
                int code = httpCon.getResponseCode();

                if (code == HttpURLConnection.HTTP_OK) {
                    return true;
                }

                if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                    return false;
                }

                if (code == HttpURLConnection.HTTP_BAD_METHOD) {
                    con = url.openConnection();
                    applyCaches(con);
                    if (con instanceof HttpURLConnection) {
                        HttpURLConnection newHttpCon = (HttpURLConnection) con;
                        code = newHttpCon.getResponseCode();

                        if (code == HttpURLConnection.HTTP_OK) {
                            return true;
                        }

                        if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                            return false;
                        }

                        httpCon = newHttpCon;
                    }
                }
            }

            if (con instanceof JarURLConnection) {
                JarURLConnection jarCon = (JarURLConnection) con;
                JarFile jarFile = jarCon.getJarFile();

                try {
                    return (jarCon.getEntryName() == null || jarCon.getJarEntry() != null);

                } finally {
                    if (!jarCon.getUseCaches()) {
                        jarFile.close();
                    }
                }

            } else if (con.getContentLengthLong() > 0) {
                return true;
            }

            if (httpCon != null) {
                // HTTP response code is not OK or NOT_FOUND, and content length is not positive - consider that the resource does not exist.
                httpCon.disconnect();
                return false;
            }

            // try to read from the stream
            getInputStream().close();
            return true;

        } catch (IOException ex) {
            // Consider that the resource does not exist when we can't read from the stream or when there is a URL connection issue.
            return false;
        }
    }

    @Override
    public long contentLength() throws IOException {
        URL url = getURL();

        if (FileUtils.isFileURL(url)) {

            // Proceed with file system resolution
            File file = getFile();
            long length = file.length();
            if (length == 0L && !file.exists()) {
                throw new FileNotFoundException("Resource cannot be resolved: " + url);
            }
            return length;
        }

        // Try a URL connection content-length header
        URLConnection con = url.openConnection();
        applyCaches(con);

        if (con instanceof HttpURLConnection) {
            ((HttpURLConnection) con).setRequestMethod("HEAD");
        }

        long length = con.getContentLengthLong();

        if (length <= 0
                && con instanceof HttpURLConnection
                && ((HttpURLConnection) con).getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD
        ) {
            con = url.openConnection();
            applyCaches(con);
            length = con.getContentLengthLong();
        }
        return length;
    }

    @Override
    public boolean isReadable() {
        try {
            return isReadable(getURL());
        } catch (IOException ex) {
            return false;
        }
    }

    protected boolean isReadable(URL url) throws IOException {
        URLConnection con = url.openConnection();
        applyCaches(con);

        if (con instanceof HttpURLConnection) {
            HttpURLConnection httpCon = (HttpURLConnection) con;

            httpCon.setRequestMethod("HEAD");
            int code = httpCon.getResponseCode();

            if (code == HttpURLConnection.HTTP_BAD_METHOD) {
                con = url.openConnection();
                applyCaches(con);

                if (!(con instanceof HttpURLConnection)) {
                    return false;
                }

                HttpURLConnection newHttpCon = (HttpURLConnection) con;
                code = newHttpCon.getResponseCode();

                if (code != HttpURLConnection.HTTP_OK) {
                    newHttpCon.disconnect();
                    return false;
                }
            } else if (code != HttpURLConnection.HTTP_OK) {
                httpCon.disconnect();
                return false;
            }
        } else if (con instanceof JarURLConnection) {
            JarEntry jarEntry = ((JarURLConnection) con).getJarEntry();
            return jarEntry != null && !jarEntry.isDirectory();
        }

        long contentLength = con.getContentLengthLong();

        if (contentLength > 0) {
            return true;
        }

        if (contentLength == 0) {
            // Empty file or directory -> not readable
            return false;
        }

        getInputStream().close();
        return true;
    }

    @Override
    public long lastModified() throws IOException {
        URL url = getURL();
        boolean fileCheck = false;

        if (FileUtils.isFileURL(url) || FileUtils.isJarURL(url)) {

            // Proceed with file system resolution
            fileCheck = true;

            try {
                File fileToCheck = getFileForLastModifiedCheck();
                long lastModified = fileToCheck.lastModified();

                if (lastModified > 0L || fileToCheck.exists()) {
                    return lastModified;
                }
            } catch (FileNotFoundException ex) {
                // Ignore - probably a JAR resource, not resolvable in the file system
            }
        }

        // Try a URL connection last-modified header
        URLConnection con = url.openConnection();
        applyCaches(con);

        if (con instanceof HttpURLConnection) {
            ((HttpURLConnection) con).setRequestMethod("HEAD");
        }

        long lastModified = con.getLastModified();

        if (lastModified == 0) {

            if (con instanceof HttpURLConnection && ((HttpURLConnection) con).getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD) {

                con = url.openConnection();
                applyCaches(con);
                lastModified = con.getLastModified();
            }

            if (fileCheck && con.getContentLengthLong() <= 0) {
                throw new FileNotFoundException("Resource cannot be resolved: " + url);
            }
        }

        return lastModified;
    }

    protected void applyCaches(@NotNull URLConnection connection) {

        if (!(connection instanceof JarURLConnection)) {
            connection.setUseCaches(shouldApplyCaches());
        }

        if (connection instanceof HttpURLConnection) {
            connection.setUseCaches(shouldApplyCaches());
        }
    }

    protected abstract boolean shouldApplyCaches();

}
