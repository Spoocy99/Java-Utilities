package dev.spoocy.jda.event;
import dev.spoocy.jda.commands.DiscordCommand;
import dev.spoocy.jda.commands.data.CommandData;
import dev.spoocy.jda.commands.data.SubCommandData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandPreProcessEvent implements Cancellable {

    private final CommandData data;
    private final SubCommandData subCommand;
    private final CommandEvent event;

    private boolean cancelled;

    public CommandPreProcessEvent(@NotNull CommandData data,
                                  @NotNull SubCommandData subCommand,
                                  @NotNull CommandEvent event) {
        this.data = data;
        this.subCommand = subCommand;
        this.event = event;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.cancelled = canceled;
    }

    public DiscordCommand getCommandInstance() {
        return this.data.getInstance();
    }

    public CommandData getCommandData() {
        return data;
    }

    public SubCommandData getSubCommandData() {
        return this.subCommand;
    }

    public CommandEvent getCommandEvent() {
        return event;
    }
}
