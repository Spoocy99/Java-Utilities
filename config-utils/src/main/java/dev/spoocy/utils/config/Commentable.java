package dev.spoocy.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Commentable {

    /**
     * Checks if this config supports comments.
     *
     * @return {@code true} if this config supports comments, {@code false} otherwise.
     */
    default boolean isCommentable() {
        return false;
    }

    /**
     * Gets the comments of the specified type and path.
     *
     * @param type the type of comments to get.
     * @param path the path to get the comments of, or an empty string for header comments.
     *
     * @return a list of comments for the specified type and path, or an empty list if there are no comments for the specified type and path.
     *
     * @throws UnsupportedOperationException if this config does not support comments.
     */
    default List<String> getComments(@NotNull CommentType type, @NotNull String path) {
        throw new UnsupportedOperationException("This config does not support comments.");
    }

    /**
     * Sets the comments of the specified type and path.
     *
     * @param type     the type of comments to set.
     * @param path     the path to set the comments of, or an empty string for header comments.
     * @param comments the comments to set for the specified type and path, or {@code null} to remove all comments for the specified type and path.
     *
     * @throws UnsupportedOperationException if this config does not support comments.
     */
    default void setComments(@NotNull CommentType type, @NotNull String path, @Nullable Collection<String> comments) {
        throw new UnsupportedOperationException("This config does not support comments.");
    }

    /**
     * Gets the header comments of this config.
     *
     * @return a list of header comments, or an empty list if there are no header comments.
     */
    default List<String> getHeaderComments() {
        return getComments(CommentType.HEADER, "");
    }

    /**
     * Gets the comments of the specified path.
     *
     * @param path the path to get the comments of.
     *
     * @return a list of comments for the specified path, or an empty list if there are no comments for the specified path.
     */
    default List<String> getComments(@NotNull final String path) {
        return getComments(CommentType.PATH, path);
    }

    /**
     * Gets the inline comments of the specified path.
     *
     * @param path the path to get the inline comments of.
     *
     * @return a list of inline comments for the specified path, or an empty list if there are no inline comments for the specified path.
     */
    default List<String> getInlineComments(@NotNull final String path) {
        return getComments(CommentType.INLINE, path);
    }

    /**
     * Sets the header comments of this config.
     *
     * @param comments the header comments to set, or {@code null} to remove all header comments.
     */
    default void setHeaderComments(@Nullable List<String> comments) {
        setComments(CommentType.HEADER, "", comments);
    }

    /**
     * Sets the comments of the specified path.
     *
     * @param path     the path to set the comments of.
     * @param comments the comments to set for the specified path, or {@code null} to remove all comments for the specified path.
     */
    default void setComments(@NotNull String path, @Nullable List<String> comments) {
        setComments(CommentType.PATH, path, comments);
    }

    /**
     * Sets the inline comments of the specified path.
     *
     * @param path     the path to set the inline comments of.
     * @param comments the inline comments to set for the specified path, or {@code null} to remove all inline comments for the specified path.
     */
    default void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        setComments(CommentType.INLINE, path, comments);
    }

    /**
     * Sets the header comments of this config.
     *
     * @param comments the header comments to set, or an empty array to remove all header comments.
     */
    default void setHeaderComments(@NotNull String... comments) {
        setHeaderComments(comments == null || comments.length == 0
                ? null : List.of(comments));
    }

    /**
     * Sets the comments of the specified path.
     *
     * @param path     the path to set the comments of.
     * @param comments the comments to set for the specified path, or an empty array to remove all comments for the specified path.
     */
    default void setComments(@NotNull final String path, @NotNull String... comments) {
        setComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }

    /**
     * Sets the inline comments of the specified path.
     *
     * @param path     the path to set the inline comments of.
     * @param comments the inline comments to set for the specified path, or an empty array to remove all inline comments for the specified path.
     */
    default void setInlineComments(@NotNull final String path, @NotNull String... comments) {
        setInlineComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }

}
