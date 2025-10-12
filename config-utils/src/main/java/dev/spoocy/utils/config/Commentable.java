package dev.spoocy.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Commentable {

    List<String> getHeaderComments();

    void setHeaderComments(@Nullable final List<String> comments);

    default void setHeaderComments(String... comments) {
        setHeaderComments(comments == null || comments.length == 0
                ? null : List.of(comments));
    }


    List<String> getComments(@NotNull final String path);

    void setComments(@NotNull final String path, @Nullable final List<String> comments);

    default void setComments(@NotNull final String path, String... comments) {
        setComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }


    List<String> getInlineComments(@NotNull final String path);

    void setInlineComments(@NotNull final String path, @Nullable final List<String> comments);

    default void setInlineComments(@NotNull final String path, String... comments) {
        setInlineComments(path, comments == null || comments.length == 0
                ? null : List.of(comments));
    }

}
