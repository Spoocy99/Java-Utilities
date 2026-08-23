package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.AbstractConfig;
import dev.spoocy.utils.config.loader.YamlProcessor;
import dev.spoocy.utils.config.nodes.NodeTree;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class YamlConfig extends AbstractConfig {

    protected final YamlSettings settings;

    public YamlConfig() {
        this(s -> {});
    }

    public YamlConfig(@NotNull Consumer<YamlSettings> settingsEditor) {
        super();
        this.settings = new YamlSettings(this);
        settingsEditor.accept(this.settings);
    }

    @Override
    public @NotNull YamlSettings settings() {
        return this.settings;
    }

    @Override
    public @NotNull String saveToString(@NotNull Representer representer) {
        YamlProcessor processor = this.settings.processor();
        processor.applyOptions(this.settings);

        NodeTree tree = representer.createTree(this);
        MappingNode node = toYamlTree(tree, processor);

        node.setBlockComments(getCommentLines(saveHeader(this.header), CommentType.BLOCK, false));
        node.setEndComments(getCommentLines(this.footer, CommentType.BLOCK, false));

        StringWriter writer = new StringWriter();
        if (node.getBlockComments().isEmpty() && node.getEndComments().isEmpty() && node.getValue().isEmpty()) {
            writer.write("");
        } else {

            if (node.getValue().isEmpty()) {
                node.setFlowStyle(DumperOptions.FlowStyle.FLOW);
            }

            processor.serialize(node, writer);
        }
        return writer.toString();
    }

    private Node toYamlNode(@NotNull dev.spoocy.utils.config.nodes.Node node, @NotNull YamlProcessor processor) {
        if (node instanceof NodeTree) {
            return toYamlTree((NodeTree) node, processor);
        }

        Object value = this.unpack(node);
        return processor.represent(value);
    }

    @Contract("_, _ -> new")
    private @NotNull MappingNode toYamlTree(@NotNull NodeTree tree, @NotNull YamlProcessor processor) {
        List<NodeTuple> nodeTuples = new ArrayList<>();

        for (dev.spoocy.utils.config.nodes.NodeTuple entry : tree) {
            dev.spoocy.utils.config.nodes.Node keyNode = entry.getKeyNode();
            dev.spoocy.utils.config.nodes.Node valueNode = entry.getValueNode();

            Node yamlKey = toYamlNode(keyNode, processor);
            Node yamlValue = toYamlNode(valueNode, processor);

            yamlKey.setBlockComments(getCommentLines(valueNode.getComments(), CommentType.BLOCK, true));

            if (yamlValue instanceof MappingNode || yamlValue instanceof SequenceNode) {
                yamlKey.setInLineComments(getCommentLines(valueNode.getInlineComments(), CommentType.IN_LINE, false));
            } else {
                yamlValue.setInLineComments(getCommentLines(valueNode.getInlineComments(), CommentType.IN_LINE, false));
            }

            nodeTuples.add(new NodeTuple(yamlKey, yamlValue));
        }

        return new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
    }

    @NotNull
    private List<CommentLine> getCommentLines(@NotNull List<String> comments, @NotNull CommentType commentType, boolean blankLineBefore) {
        List<CommentLine> lines = new ArrayList<>();

        if(blankLineBefore && !comments.isEmpty()) {
            lines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));
        }

        for (String comment : comments) {
            if (comment == null) {
                lines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));
                continue;
            }

            String line = comment;
            line = line.isEmpty() ? line : " " + line;
            lines.add(new CommentLine(null, null, line, commentType));
        }

        return lines;
    }

    @NotNull
    private List<String> saveHeader(@NotNull List<String> header) {
        LinkedList<String> list = new LinkedList<>(header);

        if (!list.isEmpty()) {
            list.add(null);
        }

        return list;
    }

}
