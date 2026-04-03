package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.YamlSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class YamlProcessor {

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

        Map<String, Object> map = new java.util.LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) data).entrySet()) {
            map.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return map;
    }
}
