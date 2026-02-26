package dev.spoocy.utils.config.components;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.scheduler.Scheduler;
import dev.spoocy.utils.common.scheduler.task.Task;
import dev.spoocy.utils.config.Document;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractDocument implements Document {

    @Override
    public void save() throws IOException {
        getConfig().save(getFile());
    }

    @Override
    public boolean saveSafely() {
        try {
            save(getFile());
            return true;
        } catch (IOException e) {
            ILogger.forThisClass().error("An error occurred while saving document at " + getFile().getPath(), e);
            return false;
        }
    }

    @Override
    public Task<Void> saveAsync() {
        return Scheduler.runAsyncCallable(() -> {
            save();
            return null;
        });
    }
}
