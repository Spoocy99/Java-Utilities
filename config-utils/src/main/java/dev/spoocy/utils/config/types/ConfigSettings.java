package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.representer.SerializingRepresenter;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.serializer.Serializers;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigSettings {

    protected final Config config;
    protected Constructor constructor = SerializingConstructor.DEFAULT_INSTANCE;
    protected Representer representer = SerializingRepresenter.DEFAULT_INSTANCE;
    protected char pathSeparator = '.';
    protected Serializers resolvingSerializers = NamedSerializers.DEFAULT_INSTANCE;
    protected List<String> header = Collections.emptyList();
    protected List<String> footer = Collections.emptyList();


    public ConfigSettings(@NotNull Config config) {
        this.config = config;
    }

    @NotNull
    public Config configuration() {
        return this.config;
    }

    @NotNull
    public Constructor constructor() {
        return this.constructor;
    }

    @NotNull
    public ConfigSettings constructor(@NotNull Constructor constructor) {
        this.constructor = constructor;
        return this;
    }

    @NotNull
    public Representer representer() {
        return this.representer;
    }

    @NotNull
    public ConfigSettings representer(@NotNull Representer representer) {
        this.representer = representer;
        return this;
    }

    public char pathSeparator() {
        return this.pathSeparator;
    }

    @NotNull
    public ConfigSettings pathSeparator(char value) {
        this.pathSeparator = value;
        return this;
    }

    @NotNull
    public Serializers serializers() {
        return this.resolvingSerializers;
    }

    @NotNull
    public ConfigSettings serializers(@NotNull Serializers serializers) {
        this.resolvingSerializers = serializers;
        return this;
    }

    @NotNull
    public List<String> header() {
        return this.header;
    }

    @NotNull
    public ConfigSettings header(@NotNull List<String> header) {
        this.header = Collections.unmodifiableList(header);
        return this;
    }

    @NotNull
    public List<String> footer() {
        return this.footer;
    }

    @NotNull
    public ConfigSettings footer(@NotNull List<String> footer) {
        this.footer = Collections.unmodifiableList(footer);
        return this;
    }
}
