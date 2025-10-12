package dev.spoocy.utils.config;

import dev.spoocy.utils.common.Version.Version;
import dev.spoocy.utils.config.misc.SectionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Readable extends Json {

    Readable getSection(@NotNull String path);
    SectionList<? extends Readable> getSectionArray(@NotNull String path);

    @Nullable Object getObject(@NotNull String path);
    Object getObject(@NotNull String path, @Nullable Object defaultValue);

    <T> T get(@NotNull String path, @NotNull Class<T> clazz);
    <T> T get(@NotNull String path, @Nullable T defaultValue);

    boolean isOf(@NotNull String path, @NotNull Class<?> clazz);
    <T> T getSerializable(@NotNull String path, @Nullable T defaultValue);
    @Nullable <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz);

    boolean isString(@NotNull String path);
    String getString(@NotNull String path);
    String getString(@NotNull String path, @NotNull String defaultValue);
    @NotNull List<String> getStringList(@NotNull String path);

    boolean isInt(@NotNull String path);
    int getInt(@NotNull String path);
    int getInt(@NotNull String path, int defaultValue);
    @NotNull List<Integer> getIntegerList(@NotNull String path);

    boolean isDouble(@NotNull String path);
    double getDouble(@NotNull String path);
    double getDouble(@NotNull String path, double defaultValue);
    @NotNull List<Double> getDoubleList(@NotNull String path);

    boolean isFloat(@NotNull String path);
    float getFloat(@NotNull String path);
    float getFloat(@NotNull String path, float defaultValue);
    @NotNull List<Float> getFloatList(@NotNull String path);

    boolean isLong(@NotNull String path);
    long getLong(@NotNull String path);
    long getLong(@NotNull String path, long defaultValue);
    @NotNull List<Long> getLongList(@NotNull String path);

    boolean isBoolean(@NotNull String path);
    boolean getBoolean(@NotNull String path);
    boolean getBoolean(@NotNull String path, boolean defaultValue);
    @NotNull List<Boolean> getBooleanList(@NotNull String path);

    Class<?> getClass(@NotNull String path);
    Class<?> getClass(@NotNull String path, @Nullable Class<?> defaultValue);

    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz);
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue);

    UUID getUUID(@NotNull String path);
    UUID getUUID(@NotNull String path, @Nullable UUID defaultValue);

    Date getDate(@NotNull String path);
    Date getDate(@NotNull String path, @Nullable Date defaultValue);

    OffsetDateTime getOffsetDateTime(@NotNull String path);
    OffsetDateTime getOffsetDateTime(@NotNull String path, @Nullable OffsetDateTime defaultValue);

    Version getVersion(@NotNull String path);
    Version getVersion(@NotNull String path, @Nullable Version defaultValue);

    boolean isSet(@NotNull String path);

    boolean isList(@NotNull String path);
    @Nullable List<?> getList(@NotNull String path);
    List<?> getList(@NotNull String path, @Nullable List<?> defaultValue);
    <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue);

    Collection<String> keys();
    Map<String, Object> values();
    Map<String, String> valuesAsString();

}
