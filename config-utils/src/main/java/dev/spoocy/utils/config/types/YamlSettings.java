package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.loader.YamlProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class YamlSettings extends ConfigSettings {

    protected YamlProcessor processor;
    protected int indent = 2;
    protected int width;
    protected boolean prettyFlow = false;
    protected boolean comments = true;

    public YamlSettings(@NotNull Config config) {
        super(config);
        this.processor = new YamlProcessor();
    }

    @Override
    public @NotNull YamlSettings pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }

    public YamlProcessor processor() {
        if(this.processor == null) {
            this.processor = new YamlProcessor();
        }
        return this.processor;
    }

    public YamlSettings processor(@NotNull YamlProcessor processor) {
        this.processor = processor;
        return this;
    }

    public int indent() {
        return this.indent;
    }

    @NotNull
    public YamlSettings indent(int value) {
        this.indent = value;
        return this;
    }

    public int width() {
        return this.width;
    }

    @NotNull
    public YamlSettings width(int value) {
        this.width = value;
        return this;
    }

    public boolean prettyFlow() {
        return this.prettyFlow;
    }

    @NotNull
    public YamlSettings prettyFlow(boolean value) {
        this.prettyFlow = value;
        return this;
    }

    public boolean comments() {
        return this.comments;
    }

    @NotNull
    public YamlSettings comments(boolean value) {
        this.comments = value;
        return this;
    }


}
