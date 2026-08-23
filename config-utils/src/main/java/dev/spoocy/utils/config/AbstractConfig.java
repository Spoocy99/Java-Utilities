package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.nodes.*;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

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
    public @NotNull List<String> getFooterComments() {
        return this.footer;
    }

    @Override
    public @NotNull List<String> getHeaderComments() {
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

    @NotNull
    protected Map<?, ?> representAsMap(@NotNull Representer representer) {
        NodeTree tree = representer.createTree(this);
        return unpackMap(tree);
    }

    protected Object unpack(@NotNull Node node) {
        if (node instanceof NodeTree) {
            return unpackMap((NodeTree) node);
        }

        if (node instanceof SequenceNode) {
            return unpackSequence((SequenceNode) node);
        }

        if (node instanceof ScalarNode) {
            return unpackScalar((ScalarNode) node);
        }

        throw new IllegalStateException("Unsupported node type: " + node.getClass().getName());
    }

    protected Map<?, ?> unpackMap(@NotNull NodeTree tree) {
        Map<Object, Object> values = new LinkedHashMap<>();

        for (NodeTuple tuple : tree) {
            Object key = unpack(tuple.getKeyNode());
            Object value = unpack(tuple.getValueNode());
            values.put(key, value);
        }

        return values;
    }

    protected Object unpackSequence(@NotNull SequenceNode node) {
        List<Object> values = new LinkedList<>();

        for (Node item : node) {
            Object value = unpack(item);
            values.add(value);
        }

        return values;
    }

    @Nullable
    protected Object unpackScalar(@NotNull ScalarNode node) {
        return node.getData();
    }

}
