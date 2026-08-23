package dev.spoocy.utils.config.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MemoryData extends ConfigData {

    @NotNull
    protected Class<?> type;

    @Nullable
    protected Object data;

    public MemoryData(@Nullable List<String> comments, @Nullable List<String> inlineComments) {
        super(comments, inlineComments);
        this.type = Node.NULL_TYPE;
        this.data = null;
    }

    @NotNull
    public Class<?> getType() {
        return this.type;
    }

    public boolean hasData() {
        return this.data != null;
    }

    @Nullable
    public Object getData() {
        return this.data;
    }

    public void setData(@Nullable Object data) {

        if (data == null) {
            this.type = Node.NULL_TYPE;
            this.data = null;
        } else {
            this.type = data.getClass();
            this.data = data;
        }

    }

}

