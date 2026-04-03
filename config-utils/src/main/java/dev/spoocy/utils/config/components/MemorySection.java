package dev.spoocy.utils.config.components;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.config.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MemorySection extends AbstractDataHolder implements ConfigSection {

    protected final Map<String, ConfigNode> map = new LinkedHashMap<>();
    private final Config root;
    private final ConfigSection parent;
    private final String path;
    private final String fullPath;

    public MemorySection() {
        if (!(this instanceof Config)) {
            throw new IllegalStateException("MemorySection must be a Config if no parent is provided");
        }
        this.root = (Config) this;
        this.parent = null;
        this.path = "";
        this.fullPath = "";
    }

    public MemorySection(@NotNull ConfigSection parent, @NotNull String path) {
        this.parent = Args.notNull(parent, "parent");
        this.path = Args.notNull(path, "path");
        this.root = parent.getRoot();
        this.fullPath = createPath(parent, path);
    }

    @Override
    public @NotNull String getName() {
        return this.path;
    }

    @Override
    public @NotNull String getWorkingPath() {
        return this.fullPath;
    }

    @Override
    public @NotNull Config getRoot() {
        return this.root;
    }

    @Override
    public @Nullable ConfigSection getParent() {
        return this.parent;
    }

    public List<Pair<String, ConfigNode>> entries() {
        List<Pair<String, ConfigNode>> entries = new ArrayList<>();

        for (Map.Entry<String, ConfigNode> entry : this.map.entrySet()) {
            entries.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return entries;
    }

    @Nullable
    private ConfigNode getConfigValue(@NotNull String path) {
        PathContext context = this.resolvePath(path);

        if (context.hasNextPath()) {
            ConfigSection section = getSection(context.key);

            if (section instanceof MemorySection) {
                return ((MemorySection) section).getConfigValue(context.nextPath);
            }

            return null;
        }

        return this.map.get(context.key);
    }

    @Override
    public List<String> getHeaderComments() {
        return this.root.getHeaderComments();
    }

    @Override
    public List<String> getFooterComments() {
        return this.root.getFooterComments();
    }

    @Override
    public void setHeaderComments(@Nullable List<String> comments) {
        this.root.setHeaderComments(comments);
    }

    @Override
    public void setFooterComments(@Nullable List<String> comments) {
        this.root.setFooterComments(comments);
    }

    @Override
    public List<String> getComments(@NotNull String path) {
        ConfigNode value = getConfigValue(path);
        return value == null ? Collections.emptyList() : value.getComments();
    }

    @Override
    public List<String> getInlineComments(@NotNull String path) {
        ConfigNode value = getConfigValue(path);
        return value == null ? Collections.emptyList() : value.getInlineComments();
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        ConfigNode value = getConfigValue(path);

        if (value == null) {
            return;
        }

        value.setComments(comments);
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        ConfigNode value = getConfigValue(path);

        if (value == null) {
            return;
        }

        value.setInlineComments(comments);
    }


    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        PathContext context = this.resolvePath(path);

        if (context.hasNextPath()) {
            ConfigSection section = getSection(context.key);

            if (section == null) {

                if (value == null) {
                    // Value should be removed, but the section doesn't exist, so it is effectively removed already
                    return;
                }

                section = createSection(context.key);
            }

            section.set(context.nextPath, value);
            return;
        }

        if (value == null) {
            this.map.remove(context.key);
            return;
        }

        ConfigNode configValue = this.map.get(context.key);

        if (configValue == null) {
            this.map.put(context.key, new ConfigNode(value));
            return;
        }

        configValue.setData(value);
    }

    @Override
    public void remove(@NotNull String path) {
        PathContext context = this.resolvePath(path);

        if (context.hasNextPath()) {
            ConfigSection section = getSection(context.key);

            if (section == null) {
                return;
            }

            section.remove(context.nextPath);
            return;
        }

        this.map.remove(context.key);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return getConfigValue(path) != null;
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) {
        List<Map<String, Object>> list = this.getMapList(path);
        final List<ConfigSection> sections = new ArrayList<>();

        for (Map<String, Object> map : list) {
            ConfigSection section = new MemorySection(this, path);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                setSectionValue(section, entry.getKey(), entry.getValue());
            }
            sections.add(section);
        }

        return sections;
    }

    private void setSectionValue(@NotNull ConfigSection section, @NotNull String key, @Nullable Object value) {
        if (value instanceof Map) {
            ConfigSection child = section.createSection(key);

            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (entry.getKey() instanceof String) {
                    setSectionValue(child, (String) entry.getKey(), entry.getValue());
                }
            }
            return;
        }

        section.set(key, value);
    }

    @Override
    public @Nullable Object getObject(@NotNull String path) {
        ConfigNode value = getConfigValue(path);
        return value == null ? null : value.getData();
    }

    @Override
    public Collection<String> keys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>();

        for (String key : this.map.keySet()) {
            keys.add(key);

            if (deep) {
                ConfigSection section = getSection(key);

                if (section != null) {
                    for (String subKey : section.keys(true)) {
                        keys.add(key + root.settings()
                                .pathSeparator() + subKey);
                    }
                }
            }
        }

        return keys;
    }

    @Override
    public Map<String, Object> values(boolean deep) {
        Map<String, Object> values = new LinkedHashMap<>();

        for (Map.Entry<String, ConfigNode> entry : this.map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue()
                    .getData();

            values.put(key, value);

            if (deep) {
                ConfigSection section = getSection(key);

                if (section != null) {
                    for (Map.Entry<String, Object> subEntry : section.values(true)
                            .entrySet()) {
                        values.put(key + root.settings()
                                .pathSeparator() + subEntry.getKey(), subEntry.getValue());
                    }
                }
            }
        }

        return values;
    }

    @Override
    public boolean isSection(@NotNull String path) {
        Object value = getObject(path);
        return value instanceof ConfigSection;
    }

    @Override
    public @Nullable ConfigSection getSection(@NotNull String path) {
        Object value = getObject(path);

        if (value instanceof ConfigSection) {
            return (ConfigSection) value;
        }

        return null;
    }

    @Override
    public @NotNull ConfigSection getOrCreateSection(@NotNull String path) {
        ConfigSection section = getSection(path);

        if (section == null) {
            return createSection(path);
        }

        return section;
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path) {
        if (isSet(path)) {
            throw new IllegalArgumentException("Cannot create section at path '" + path + "' because an object already exists.");
        }

        ConfigSection section = new MemorySection(this, path);
        set(path, section);
        return section;
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        ConfigSection section = createSection(path);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException("Only String keys are supported in section maps");
            }

            section.set((String) entry.getKey(), entry.getValue());
        }

        return section;
    }

    private PathContext resolvePath(@NotNull String path) {
        final char sectionSeparator = this.root.settings().pathSeparator();
        int separatorIndex = path.indexOf(sectionSeparator);

        if (separatorIndex == -1) {
            return new PathContext(path, null);
        }

        return new PathContext(
                path.substring(0, separatorIndex),
                path.substring(separatorIndex + 1)
        );
    }

    private static final class PathContext {

        private final String key;
        private final String nextPath;

        private PathContext(@NotNull String key, @Nullable String nextPath) {
            this.key = key;
            this.nextPath = nextPath;
        }

        private boolean hasNextPath() {
            return this.nextPath != null;
        }
    }

    @NotNull
    private static String createPath(@NotNull ConfigSection section, @Nullable String key) {
        return createPath(section, key, section.getRoot());
    }

    @NotNull
    private static String createPath(@NotNull ConfigSection section, @Nullable String key, @Nullable ConfigSection relativeTo) {
        Args.notNull(section, "Cannot create path without a section");

        Config root = section.getRoot();

        char separator = root.settings().pathSeparator();

        StringBuilder builder = new StringBuilder();

        for (ConfigSection parent = section; parent != relativeTo; parent = parent.getParent()) {
            if (builder.length() > 0) {
                builder.insert(0, separator);
            }
            builder.insert(0, parent.getName());
        }

        if ((key != null) && (!key.isEmpty())) {
            if (builder.length() > 0) {
                builder.append(separator);
            }

            builder.append(key);
        }

        return builder.toString();
    }

}
