package dev.spoocy.utils.config.documents;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.Document;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DocumentFile implements Document {

    private Config document;
    private final File file;

    public DocumentFile(@NotNull Config document, @NotNull Path path) {
        this.document = document;
        this.file = path.toFile();
    }

    public DocumentFile(@NotNull Config document, @NotNull File file) {
        this.document = document;
        this.file = file;
    }

    @Override
    public @NotNull Config getConfig() {
        return this.document;
    }

    @Override
    public @NotNull File getFile() {
        return this.file;
    }

    @Override
    public @NotNull Path getPath() {
        return this.file.toPath();
    }

    @Override
    public void reload() {
        this.document = Config.readFile(this.document.getClass(), this.file);
    }
}
