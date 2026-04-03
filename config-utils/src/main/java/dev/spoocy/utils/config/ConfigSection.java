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
     * Gets the path of this section.
     *
     * @return The path of this section.
     */
    @NotNull
    String getWorkingPath();

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
     *
     * @param path The path to check for being a section. Must not be null.
     * @return {@code true} if the specified path corresponds to a section, {@code false} otherwise.
     */
    boolean isSection(@NotNull String path);

    /**
     * Gets the section at the specified path.
     *
     * @param path The path of the section to get, relative to this section.
     *
     * @return The section at the specified path, or {@code null} if there is no section at the specified path.
     */
    @Nullable
    ConfigSection getSection(@NotNull String path);

    /**
     * Retrieves the section at the specified path or an empty section if no section exists at the path.
     *
     * @param path The path to retrieve the section from. Must not be null.
     * @return The section at the specified path if it exists, or an empty section otherwise. Never null.
     */
    @NotNull
    ConfigSection getOrCreateSection(@NotNull String path);

    /**
     * Creates a new section at the specified path.
     *
     * @param path The path of the section to create, relative to this section.
     *
     * @return The newly created section.
     *
     * @throws IllegalArgumentException if there is already a object at the specified path.
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
     *
     * @throws IllegalArgumentException if there is already an object at the specified path.
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
