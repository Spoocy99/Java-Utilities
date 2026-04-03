package dev.spoocy.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Commentable {

    /**
     * Gets the header comments of this config.
     *
     * @return a list of header comments, or an empty list if there are no header comments.
     */
    List<String> getHeaderComments();

    /**
     * Gets the footer comments of this config.
     *
     * @return a list of footer comments, or an empty list if there are no footer comments.
     */
    List<String> getFooterComments();

    /**
     * Gets the comments of the specified path.
     *
     * @param path the path to get the comments of.
     *
     * @return a list of comments for the specified path, or an empty list if there are no comments for the specified path.
     */
    List<String> getComments(@NotNull final String path);

    /**
     * Gets the inline comments of the specified path.
     *
     * @param path the path to get the inline comments of.
     *
     * @return a list of inline comments for the specified path, or an empty list if there are no inline comments for the specified path.
     */
    List<String> getInlineComments(@NotNull final String path);

    /**
     * Sets the header comments of this config.
     *
     * @param comments the header comments to set, or {@code null} to remove all header comments.
     */
    void setHeaderComments(@Nullable List<String> comments);

    /**
     * Sets the footer comments of this config.
     *
     * @param comments the footer comments to set, or {@code null} to remove all footer comments.
     */
    void setFooterComments(@Nullable List<String> comments);

    /**
     * Sets the comments of the specified path.
     *
     * @param path     the path to set the comments of.
     * @param comments the comments to set for the specified path, or {@code null} to remove all comments for the specified path.
     */
    void setComments(@NotNull String path, @Nullable List<String> comments);

    /**
     * Sets the inline comments of the specified path.
     *
     * @param path     the path to set the inline comments of.
     * @param comments the inline comments to set for the specified path, or {@code null} to remove all inline comments for the specified path.
     */
    void setInlineComments(@NotNull String path, @Nullable List<String> comments);

    /**
     * @see #setHeaderComments(List)
      */
    default void setHeaderComments(@NotNull String... comments) {
        setHeaderComments(comments == null || comments.length == 0
                ? null : List.of(comments));
    }

    /**
     * @see #setHeaderComments(String...)
     */
    default void setFooterComments(@NotNull String... comments) {
        setFooterComments(comments == null || comments.length == 0
                ? null : List.of(comments));
    }

    /**
     * @see #setComments(String, List)
     */
    default void setComments(@NotNull final String path, @NotNull String... comments) {
        setComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }

    /**
     * @see #setInlineComments(String, List)
     */
    default void setInlineComments(@NotNull final String path, @NotNull String... comments) {
        setInlineComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }

}
