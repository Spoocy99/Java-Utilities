package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.SerializerAssignable;
import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.YamlConfig;
import dev.spoocy.utils.config.types.YamlSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dev.spoocy.utils.config.ConfigSection;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class YamlConfigLoader implements ConfigLoader<YamlConfig, YamlSettings> {

    public static final YamlConfigLoader INSTANCE = new YamlConfigLoader();

    private final YamlProcessor processor = new YamlProcessor();

    private YamlConfigLoader() { }

    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"yml", "yaml"};
    }

    @Override
    public YamlConfig createEmpty(@NotNull Consumer<YamlSettings> settingsEditor) {
        return new YamlConfig(settingsEditor);
    }

    @Override
    public YamlConfig load(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @NotNull Consumer<YamlSettings> settingsEditor
    ) throws IOException{
        checkDependency();

        YamlConfig config = createEmpty(settingsEditor);

        // apply settings
        config.settings().constructor(constructor);
        if(constructor instanceof SerializerAssignable) {
            config.settings().serializers(((SerializerAssignable) constructor).getSerializers());
        }

        String contents = resource.getContentAsString(StandardCharsets.UTF_8);
        loadFromString(config, contents, constructor);
        return config;
    }

    private static void checkDependency() {
        try {
            Class.forName("org.yaml.snakeyaml.Yaml");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SnakeYAML could not be found in the classpath.");
        }
    }

    public void loadFromString(@NotNull YamlConfig config, @NotNull String contents) throws IOException {
        loadFromString(config, contents, SerializingConstructor.DEFAULT_INSTANCE);
    }

    private void loadFromString(
            @NotNull YamlConfig config,
            @NotNull String contents,
            @NotNull Constructor constructor
    ) throws IOException {
        this.processor.applyOptions(config.settings());

        config.clear();
        config.setHeaderComments(List.of());
        config.setFooterComments(List.of());

        MappingNode node = composeRootNode(this.processor, contents);
        if (node != null) {
            adjustNodeComments(node);
            config.setHeaderComments(loadHeader(getCommentLines(node.getBlockComments())));
            config.setFooterComments(getCommentLines(node.getEndComments()));
        }

        Object loaded = this.processor.loadFromString(contents);
        if (!(loaded instanceof Map)) {
            return;
        }

        Map<Object, Object> mapping = new LinkedHashMap<>();
        mapping.putAll((Map<?, ?>) loaded);
        constructor.constructMappings(config, mapping);

        if (node != null) {
            applyNodeComments(node, config);
        }
    }

    @Nullable
    private MappingNode composeRootNode(@NotNull YamlProcessor processor, @NotNull String contents) throws IOException {
        if (contents.trim().isEmpty()) {
            return null;
        }

        Node rawNode;
        try (Reader reader = new UnicodeReader(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)))) {
            rawNode = processor.yaml.compose(reader);
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
                ConfigSection child = section.getSection(key);
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
