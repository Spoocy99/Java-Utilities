package dev.spoocy.utils.config.types;


import dev.spoocy.utils.config.components.AbstractConfig;
import dev.spoocy.utils.config.components.MemorySection;
import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.loader.YamlProcessor;
import dev.spoocy.utils.config.representer.SerializingRepresenter;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
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

        MappingNode node = toNodeTree(this, processor, representer);

        node.setBlockComments(getCommentLines(saveHeader(this.header), CommentType.BLOCK));
        node.setEndComments(getCommentLines(this.footer, CommentType.BLOCK));

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

    private MappingNode toNodeTree(@NotNull MemorySection section, @NotNull YamlProcessor processor, @NotNull Representer representer) {
        List<NodeTuple> nodeTuples = new ArrayList<>();

        for (Pair<String, ConfigNode> entry : section.entries()) {
            String key = entry.first();
            ConfigNode node = entry.second();

            Node keyNode = processor.represent(key);
            Node valueNode;

            Object nodeData = node.getData();
            dev.spoocy.utils.config.serializer.Tag tag = node.getTag();
            
            if (tag == dev.spoocy.utils.config.serializer.Tag.SECTION && nodeData instanceof MemorySection) {
                // Recursively handle subsections
                valueNode = toNodeTree((MemorySection) nodeData, processor, representer);
            } else {
                // Use representer for non-section values
                Object represented = representer.represent(node);
                valueNode = processor.represent(represented);
            }

            keyNode.setBlockComments(getCommentLines(section.getComments(key), CommentType.BLOCK));
            if (valueNode instanceof MappingNode || valueNode instanceof SequenceNode) {
                keyNode.setInLineComments(getCommentLines(section.getInlineComments(key), CommentType.IN_LINE));
            } else {
                valueNode.setInLineComments(getCommentLines(section.getInlineComments(key), CommentType.IN_LINE));
            }

            nodeTuples.add(new NodeTuple(keyNode, valueNode));
        }

        return new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
    }

    private List<CommentLine> getCommentLines(@NotNull List<String> comments, @NotNull CommentType commentType) {
        List<CommentLine> lines = new ArrayList<>();

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

    private List<String> saveHeader(@NotNull List<String> header) {
        LinkedList<String> list = new LinkedList<>(header);

        if (!list.isEmpty()) {
            list.add(null);
        }

        return list;
    }

}
