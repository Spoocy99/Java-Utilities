package dev.spoocy.utils.config.components;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.types.ConfigSettings;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class FileDocument implements Document {

    private final Resource relation;
    private final Config config;

    public FileDocument(@NotNull Config config, @NotNull Resource relation) {
        this.config = config;
        this.relation = relation;
    }

    @Override
    public @NotNull Resource getRelation() {
        return this.relation;
    }

    @Override
    public void save() throws IOException {
        if (this.relation instanceof WriteableResource) {
            this.save((WriteableResource) this.relation, this.settings().representer());
            return;
        }

        throw new IOException("Relation is not writable.");
    }

    @Override
    public @NotNull ConfigSettings settings() {
        return this.config.settings();
    }

    @Override
    public @NotNull String saveToString(@NotNull Representer representer) {
        return this.config.saveToString(representer);
    }

    @Override
    public void save(@NotNull WriteableResource file, @NotNull Representer representer) throws IOException {
        this.config.save(file, representer);
    }

    @Override
    public @NotNull Document withRelation(@NotNull Resource resource) {
        return this.config.withRelation(resource);
    }

    @Override
    public @NotNull String getName() {
        return this.config.getName();
    }

    @Override
    public @NotNull String getWorkingPath() {
        return this.config.getWorkingPath();
    }

    @Override
    public @NotNull Config getRoot() {
        return this.config.getRoot();
    }

    @Override
    public @Nullable ConfigSection getParent() {
        return this.config.getParent();
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return this.config.isSection(path);
    }

    @Override
    public @Nullable ConfigSection getSection(@NotNull String path) {
        return this.config.getSection(path);
    }

    @Override
    public @NotNull ConfigSection getOrCreateSection(@NotNull String path) {
        return this.config.getOrCreateSection(path);
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path) {
        return this.config.createSection(path);
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return this.config.createSection(path, map);
    }

    @Override
    public @Nullable Object getObject(@NotNull String path) {
        return this.config.getObject(path);
    }

    @Override
    public @Nullable Object getObject(@NotNull String path, @Nullable Object defaultValue) {
        return this.config.getObject(path, defaultValue);
    }

    @Override
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz) {
        return this.config.get(path, clazz);
    }

    @Override
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        return this.config.get(path, clazz, defaultValue);
    }

    @Override
    public boolean is(@NotNull String path, @NotNull Class<?> clazz) {
        return this.config.is(path, clazz);
    }

    @Override
    public <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        return this.config.getSerializable(path, clazz, defaultValue);
    }

    @Override
    public @Nullable <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return this.config.getSerializable(path, clazz);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return this.config.isString(path);
    }

    @Override
    public @NotNull String getString(@NotNull String path, @Nullable String defaultValue) {
        return this.config.getString(path, defaultValue);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return this.config.isInt(path);
    }

    @Override
    public int getInt(@NotNull String path, int defaultValue) {
        return this.config.getInt(path, defaultValue);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return this.config.isDouble(path);
    }

    @Override
    public double getDouble(@NotNull String path, double defaultValue) {
        return this.config.getDouble(path, defaultValue);
    }

    @Override
    public boolean isFloat(@NotNull String path) {
        return this.config.isFloat(path);
    }

    @Override
    public float getFloat(@NotNull String path, float defaultValue) {
        return this.config.getFloat(path, defaultValue);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return this.config.isLong(path);
    }

    @Override
    public long getLong(@NotNull String path, long defaultValue) {
        return this.config.getLong(path, defaultValue);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        return this.config.isBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        return this.config.getBoolean(path, defaultValue);
    }

    @Override
    public Class<?> getClass(@NotNull String path, @Nullable Class<?> defaultValue) {
        return this.config.getClass(path, defaultValue);
    }

    @Override
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        return this.config.getEnum(path, clazz, defaultValue);
    }

    @Override
    public UUID getUUID(@NotNull String path, @Nullable UUID defaultValue) {
        return this.config.getUUID(path, defaultValue);
    }

    @Override
    public Version getVersion(@NotNull String path, @Nullable Version defaultValue) {
        return this.config.getVersion(path, defaultValue);
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return this.config.isSet(path);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return this.config.isList(path);
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) {
        return this.config.getList(path);
    }

    @Override
    public List<?> getList(@NotNull String path, @Nullable List<?> defaultValue) {
        return this.config.getList(path, defaultValue);
    }

    @Override
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        return this.config.getList(path, clazz, defaultValue);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        return this.config.getStringList(path);
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        return this.config.getBooleanList(path);
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        return this.config.getIntegerList(path);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        return this.config.getDoubleList(path);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        return this.config.getFloatList(path);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        return this.config.getLongList(path);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        return this.config.getByteList(path);
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        return this.config.getCharacterList(path);
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        return this.config.getShortList(path);
    }

    @Override
    public List<Map<String, Object>> getMapList(@NotNull String path) {
        return this.config.getMapList(path);
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) {
        return this.config.getSectionList(path);
    }

    @Override
    public Collection<String> keys(boolean deep) {
        return this.config.keys(deep);
    }

    @Override
    public Map<String, Object> values(boolean deep) {
        return this.config.values(deep);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        this.config.set(path, value);
    }

    @Override
    public void remove(@NotNull String path) {
        this.config.remove(path);
    }

    @Override
    public void clear() {
        this.config.clear();
    }

    @Override
    public void opposite(@NotNull String path) {
        this.config.opposite(path);
    }

    @Override
    public void multiply(@NotNull String path, double value) {
        this.config.multiply(path, value);
    }

    @Override
    public void divide(@NotNull String path, double value) {
        this.config.divide(path, value);
    }

    @Override
    public void add(@NotNull String path, double value) {
        this.config.add(path, value);
    }

    @Override
    public void subtract(@NotNull String path, double value) {
        this.config.subtract(path, value);
    }

    @Override
    public List<String> getHeaderComments() {
        return this.config.getHeaderComments();
    }

    @Override
    public List<String> getFooterComments() {
        return this.config.getFooterComments();
    }

    @Override
    public List<String> getComments(@NotNull String path) {
        return this.config.getComments(path);
    }

    @Override
    public List<String> getInlineComments(@NotNull String path) {
        return this.config.getInlineComments(path);
    }

    @Override
    public void setHeaderComments(@Nullable List<String> comments) {
        this.config.setHeaderComments(comments);
    }

    @Override
    public void setFooterComments(@Nullable List<String> comments) {
        this.config.setFooterComments(comments);
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        this.config.setComments(path, comments);
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        this.config.setInlineComments(path, comments);
    }
}
