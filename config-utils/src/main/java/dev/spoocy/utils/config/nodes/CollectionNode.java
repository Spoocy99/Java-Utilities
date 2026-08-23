package dev.spoocy.utils.config.nodes;

import dev.spoocy.utils.config.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class CollectionNode<T> extends Node implements Iterable<T> {

    public CollectionNode(
            @NotNull Tag tag,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        super(tag, comments, inlineComments);
    }

    public abstract List<T> getValue();

    @Override
    public abstract @NotNull Iterator<T> iterator();
}
