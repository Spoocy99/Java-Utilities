package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

/**
 * URL based {@link Resource} implementation.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class UrlResource extends ResolvableResource {

    public static UrlResource of(@NotNull URI uri) throws UncheckedIOException {
		try {
			return new UrlResource(uri);
		}
		catch (MalformedURLException ex) {
			throw new UncheckedIOException(ex);
		}
	}

    @NotNull
	private final URL url;

    @Nullable
	private final URI uri;

    @Nullable
	private volatile String cleanedUrl;

    @Nullable
	private volatile Boolean useCaches;

    public UrlResource(@NotNull URL url) {
		this.uri = null;
		this.url = url;
	}

	public UrlResource(@NotNull URI uri) throws MalformedURLException {
		this.uri = uri;
		this.url = uri.toURL();
	}

    public UrlResource(@NotNull String path) throws MalformedURLException {
		String cleanedPath = FileUtils.cleanPath(path);
		URI uri;
		URL url;

		try {
			uri = FileUtils.toURI(cleanedPath);
			url = uri.toURL();
		}
		catch (URISyntaxException | IllegalArgumentException ex) {
			uri = null;
			url = FileUtils.toURL(path);
		}

		this.uri = uri;
		this.url = url;
		this.cleanedUrl = cleanedPath;
	}


    @Override
    protected String getDescription() {
        return "URL Resource [" + StringUtils.nullSafe(this.cleanedUrl, this.url.toString()) + "]";
    }

    @Override
	public URL getURL() {
		return this.url;
	}

    @Override
	public URI getURI() throws IOException {
		if (this.uri != null) {
			return this.uri;
		}
		else {
			return super.getURI();
		}
	}

    @Override
	public boolean isFile() {
		if (this.uri == null) {
			return false;
		}
		return this.uri.getScheme().equals("file");
	}

    @Override
	public File getFile() throws IOException {
		if (this.uri != null && this.uri.getScheme().equals("file")) {
            return new File(this.uri);
        }
        throw new FileNotFoundException("Resource cannot be resolved: " + this.url);
	}

    @Override
    public @NotNull String getFilename() {
        return FileUtils.getFileName(this.url.getPath(), true);
    }

    @Override
    public Path getPath() throws IOException {
        if (this.uri != null) {
            return Path.of(this.uri);
        }
        throw new FileNotFoundException("Resource cannot be resolved: " + this.url);
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
		applyCaches(con);

		final HttpURLConnection httpCon = (con instanceof HttpURLConnection) ? (HttpURLConnection) con : null;

		InputStream in = con.getInputStream();

		if (httpCon == null) {
			// For non-HTTP connections return the raw stream
			return in;
		}

		// For HTTP connections return a wrapper stream that disconnects when closed
		return new FilterInputStream(in) {
			@Override
			public void close() throws IOException {
				super.close();
				try {
					httpCon.disconnect();
				} catch (Exception ignored) {
					// ignore
				}
			}
		};
	}

    @Override
	public Resource createRelative(@NotNull String relativePath) throws MalformedURLException {
		UrlResource resource = new UrlResource(createRelativeURL(relativePath));
		resource.useCaches = this.useCaches;
		return resource;
	}

    protected URL createRelativeURL(String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return FileUtils.toRelativeURL(this.url, relativePath);
	}

    @Override
    protected void applyCaches(@NotNull URLConnection connection) {
        super.applyCaches(connection);

        String userInfo = this.url.getUserInfo();
		if (userInfo != null) {
			String encodedCredentials = Base64.getEncoder().encodeToString(userInfo.getBytes(StandardCharsets.UTF_8));
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
		}
    }

    @Override
    protected boolean shouldApplyCaches() {
        if (this.useCaches != null) {
            return this.useCaches;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }
}

