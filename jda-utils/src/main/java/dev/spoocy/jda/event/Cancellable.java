package dev.spoocy.jda.event;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean canceled);

}
