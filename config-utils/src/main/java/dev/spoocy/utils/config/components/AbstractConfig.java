package dev.spoocy.utils.config.components;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractConfig extends MemorySection implements Config {

    protected List<String> header = Collections.emptyList();
    protected List<String> footer = Collections.emptyList();

    public AbstractConfig() {
        super();
    }

    @Override
    public void save(@NotNull WriteableResource file, @NotNull Representer representer) throws IOException {
        Args.notNull(file, "File cannot be null");
        Args.notNull(representer, "Representer cannot be null");
//        if (file.exists() && !file.isWritable()) {
//            throw new IOException("Cannot write to file: " + file.getFile().getPath());
//        }

        // Ensure parent directories exist before attempting to write.
        // If the parent directories cannot be created due to permissions or other I/O issues, this
        // will throw an IOException which we propagate to the caller.
        FileUtils.createParentDirs(file.getFile());

        // Attempt to open the output stream and write. If the file cannot be written
        // due to permissions or other I/O issues, the underlying calls will throw
        // an IOException which we propagate to the caller.
        try (Writer writer = new OutputStreamWriter(file.getOutputStream())) {
            writer.write(saveToString(representer));
        }
    }

    @Override
    public @NotNull Document withRelation(@NotNull Resource resource) {
        return new FileDocument(this, resource);
    }

    @Override
    public List<String> getFooterComments() {
        return this.footer;
    }

    @Override
    public List<String> getHeaderComments() {
        return this.header;
    }

    @Override
    public void setHeaderComments(@Nullable List<String> comments) {
        this.header = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    @Override
    public void setFooterComments(@Nullable List<String> comments) {
        this.footer = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    protected static Map<String, Object> serializedValues(
            @NotNull ConfigSection section,
            @NotNull Representer representer
    ) {
        if (section instanceof MemorySection) {
            return representer.represent((MemorySection) section);
        }
        return new LinkedHashMap<>();
    }

}
