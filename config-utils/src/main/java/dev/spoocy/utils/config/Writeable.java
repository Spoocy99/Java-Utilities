package dev.spoocy.utils.config;

import dev.spoocy.utils.config.misc.SectionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Writeable extends Readable {

    void setReadOnly();

    boolean isReadonly();

	void set(@NotNull String path, @Nullable Object value);

	void remove(@NotNull String path);

	void clear();

    void opposite(@NotNull String path);
    void multiply(@NotNull String path, double value);
    void divide(@NotNull String path, double value);
    void add(@NotNull String path, double value);
    void subtract(@NotNull String path, double value);

    @Override
    Readable getSection(@NotNull String path);

    @Override
    SectionList<? extends Writeable> getSectionArray(@NotNull String path);


}
