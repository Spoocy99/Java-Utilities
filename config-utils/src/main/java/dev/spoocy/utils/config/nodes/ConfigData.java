package dev.spoocy.utils.config.nodes;

import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ConfigData {

    @NotNull
    protected List<String> comments;

    @NotNull
    protected List<String> inlineComments;

    public ConfigData(@Nullable List<String> comments, @Nullable List<String> inlineComments) {
        this.comments = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
        this.inlineComments = inlineComments == null ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);
    }

    @NotNull
    public List<String> getComments() {
        return this.comments;
    }

    @NotNull
    public List<String> getInlineComments() {
        return this.inlineComments;
    }

    public void setComments(@Nullable List<String> comments) {
        this.comments = comments == null ? Collections.emptyList() : Collections.unmodifiableList(comments);
    }

    public void setInlineComments(@Nullable List<String> inlineComments) {
        this.inlineComments = inlineComments == null ? Collections.emptyList() : Collections.unmodifiableList(inlineComments);
    }

    public MemoryData asHolder() {
        return new MemoryData(this.comments, this.inlineComments);
    }

    public MemorySection asSection(@NotNull ConfigSection parent, @NotNull String path) {
        MemorySection section = new MemorySection(parent, path);
        section.comments = this.comments;
        section.inlineComments = this.inlineComments;
        return section;
    }

}
