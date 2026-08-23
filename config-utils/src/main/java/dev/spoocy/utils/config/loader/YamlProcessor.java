package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.DefaultNodeConstructor;
import dev.spoocy.utils.config.constructor.NodeConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.YamlConfig;
import dev.spoocy.utils.config.types.YamlSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class YamlProcessor {

    protected final NodeConstructor nodeConstructor = new DefaultNodeConstructor(o -> null);

    protected final DumperOptions dumperOptions;
    protected final LoaderOptions loaderOptions;
    protected final BaseConstructor constructor;
    protected final Representer representer;
    protected final Yaml yaml;

    public YamlProcessor() {
        this.dumperOptions = createDumperOptions();
        this.loaderOptions = createLoaderOptions();
        this.constructor = createConstructor(this.loaderOptions);
        this.representer = createRepresenter(this.dumperOptions);
        this.yaml = createYaml(this.constructor, this.representer, this.loaderOptions, this.dumperOptions);
    }

    public YamlProcessor(@NotNull YamlSettings settings) {
        this();
        applyOptions(settings);
    }

    protected DumperOptions createDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return options;
    }

    protected LoaderOptions createLoaderOptions() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        options.setMaxAliasesForCollections(Integer.MAX_VALUE);
        options.setCodePointLimit(Integer.MAX_VALUE);
        options.setNestingDepthLimit(100);
        return options;
    }

    protected BaseConstructor createConstructor(@NotNull LoaderOptions loaderOptions) {
        return new SafeConstructor(loaderOptions);
    }

    protected Representer createRepresenter(@NotNull DumperOptions dumperOptions) {
        return new Representer(dumperOptions);
    }

    protected Yaml createYaml(
            @NotNull BaseConstructor constructor,
            @NotNull Representer representer,
            @NotNull LoaderOptions loaderOptions,
            @NotNull DumperOptions dumperOptions
    ) {
        return new Yaml(constructor, representer, dumperOptions, loaderOptions);
    }

    public void applyOptions(@NotNull YamlSettings settings) {
        this.dumperOptions.setPrettyFlow(settings.prettyFlow());
        this.dumperOptions.setIndent(settings.indent());
        this.dumperOptions.setWidth(settings.width());
        this.dumperOptions.setProcessComments(settings.comments());
        this.loaderOptions.setProcessComments(settings.comments());
    }

    public void serialize(@NotNull Node node, @NotNull Writer writer) {
        this.yaml.serialize(node, writer);
    }

    @NotNull
    public Node represent(@Nullable Object data) {
        return this.representer.represent(data);
    }

    @NotNull
    public Map<String, Object> load(@NotNull Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            Object data = this.yaml.load(is);
            return castMapping(data);
        }
    }

    @Nullable
    public Object loadFromString(@NotNull String contents) {
        if (contents.trim().isEmpty()) {
            return null;
        }
        return this.yaml.load(new StringReader(contents));
    }

    @NotNull
    private static Map<String, Object> castMapping(@Nullable Object data) {
        if (!(data instanceof Map)) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) data).entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }

    public void loadFromString(
            @NotNull YamlConfig config,
            @NotNull String contents,
            @NotNull Constructor constructor
    ) throws IOException {
        this.applyOptions(config.settings());

        config.clear();
        config.setHeaderComments(List.of());
        config.setFooterComments(List.of());

        MappingNode node = composeRootNode(contents);
        if (node != null) {
            adjustNodeComments(node);
            config.setHeaderComments(loadHeader(getCommentLines(node.getBlockComments())));
            config.setFooterComments(getCommentLines(node.getEndComments()));
        }

        Object loaded = this.loadFromString(contents);
        if (!(loaded instanceof Map)) {
            return;
        }

        Map<Object, Object> mapping = new LinkedHashMap<>((Map<?, ?>) loaded);
        constructor.constructMappings(config, mapping, nodeConstructor);

        if (node != null) {
            applyNodeComments(node, config);
        }
    }

    @Nullable
    private MappingNode composeRootNode(@NotNull String contents) throws IOException {
        if (contents.trim().isEmpty()) {
            return null;
        }

        Node rawNode;
        try (Reader reader = new UnicodeReader(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)))) {
            rawNode = this.yaml.compose(reader);
        }

        if (rawNode == null) {
            return null;
        }

        if (!(rawNode instanceof MappingNode)) {
            throw new InvalidObjectException("Top level is not a Map.");
        }

        return (MappingNode) rawNode;
    }

    private void adjustNodeComments(@NotNull MappingNode node) {
        if ((node.getBlockComments() == null || node.getBlockComments().isEmpty()) && !node.getValue().isEmpty()) {
            Node firstNode = node.getValue().get(0).getKeyNode();
            List<CommentLine> lines = firstNode.getBlockComments();

            if (lines != null) {
                int index = -1;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).getCommentType() == CommentType.BLANK_LINE) {
                        index = i;
                    }
                }

                if (index != -1) {
                    node.setBlockComments(lines.subList(0, index + 1));
                    firstNode.setBlockComments(lines.subList(index + 1, lines.size()));
                }
            }
        }
    }

    private void applyNodeComments(@NotNull MappingNode input, @NotNull ConfigSection section) {
        for (NodeTuple tuple : input.getValue()) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();

            if (!(keyNode instanceof ScalarNode)) {
                continue;
            }

            String key = ((ScalarNode) keyNode).getValue();

            while (valueNode instanceof AnchorNode) {
                valueNode = ((AnchorNode) valueNode).getRealNode();
            }

            section.setComments(key, getCommentLines(keyNode.getBlockComments()));
            if (valueNode instanceof MappingNode || valueNode instanceof SequenceNode) {
                section.setInlineComments(key, getCommentLines(keyNode.getInLineComments()));
            } else {
                section.setInlineComments(key, getCommentLines(valueNode.getInLineComments()));
            }

            if (valueNode instanceof MappingNode) {
                ConfigSection child = section.getSectionIfExists(key);
                if (child != null) {
                    applyNodeComments((MappingNode) valueNode, child);
                }
            }
        }
    }

    @NotNull
    private List<String> getCommentLines(@Nullable List<CommentLine> comments) {
        List<String> lines = new ArrayList<>();
        if (comments == null) {
            return lines;
        }

        for (CommentLine comment : comments) {
            if (comment.getCommentType() == CommentType.BLANK_LINE) {
                lines.add(null);
                continue;
            }

            String line = comment.getValue();
            lines.add(line.startsWith(" ") ? line.substring(1) : line);
        }

        return lines;
    }

    @NotNull
    private List<String> loadHeader(@NotNull List<String> header) {
        LinkedList<String> list = new LinkedList<>(header);

        if (!list.isEmpty()) {
            list.removeLast();
        }

        while (!list.isEmpty() && list.peek() == null) {
            list.remove();
        }

        return list;
    }

}
