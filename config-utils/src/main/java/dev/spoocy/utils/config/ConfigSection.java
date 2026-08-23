package dev.spoocy.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ConfigSection extends Writeable, Readable {

    /**
     * Gets the name of this section.
     *
     * @return The name of this section.
     */
    @NotNull
    String getName();

    /**
     * Gets the root config of this section.
     *
     * @return The root config of this section.
     */
    @NotNull
    Config getRoot();

    /**
     * Gets the parent section of this section.
     *
     * @return The parent section of this section, or {@code null} if this section is the root.
     */
    @Nullable
    ConfigSection getParent();

    /**
     * Determines whether the specified path represents a valid section.
     * A section can exist even when a certain path has a value.
     *
     * @param path The path to check for being a section. Must not be null.
     * @return {@code true} if the specified path corresponds to a section, {@code false} otherwise.
     *
     * {@link #is(String, Class)}
     */
    boolean isSection(@NotNull String path);

    /**
     * Gets the section at the specified path.
     *
     * @param path The path of the section to get, relative to this section.
     *
     * @return The section at the specified path, or {@code null} if there is no section at the specified path.
     *
     * @throws IllegalArgumentException if there is a value at the specified path that is not a section.
     */
    @NotNull
    ConfigSection getSection(@NotNull String path);

    /**
     * Gets the section at the specified path if it exists,
     * or {@code null} if it does not exist.
     *
     * @param path The path of the section to get, relative to this section.
     *
     * @return The section at the specified path, or {@code null}.
     */
    @Nullable
    ConfigSection getSectionIfExists(@NotNull String path);

    /**
     * Gets the section at the specified path if it exists,
     * or an empty section if it does not exist.
     *
     * @param path The path of the section to get, relative to this section.
     *
     * @return The section at the specified path, or an empty section.
     */
    @NotNull
    ConfigSection getSectionOrEmpty(@NotNull String path);

    /**
     * Gets the section at the specified path,
     * or creates a new section if it does not exist.
     *
     * @param path The path of the section to get or create, relative to this section.
     *
     * @return The section at the specified path, or a newly created section.
     */
    @NotNull
    ConfigSection getOrCreateSection(@NotNull String path);

    /**
     * Creates a new section at the specified path.
     *
     * @param path The path of the section to create, relative to this section.
     *
     * @return The newly created section.
     */
    @NotNull
    ConfigSection createSection(@NotNull String path);

    /**
     * Creates a new section at the specified path with the provided data.
     *
     * @param path The path where the new section should be created, relative to the current section.
     * @param map  The data to initialize the newly created section with.
     *
     * @return The newly created section.
     */
    @NotNull
    ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

    /**
     * Gets the list of sections at the specified path.
     *
     * @param path The path of the sections to get, relative to this section.
     *
     * @return The list of sections at the specified path, or an empty list if there are no sections at the specified path.
     */
    List<ConfigSection> getSectionList(@NotNull String path);
}
