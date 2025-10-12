package dev.spoocy.jda.commands;

import dev.spoocy.jda.commands.data.CommandData;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandManager {

    CommandListener getListener();

    CommandManager setListener(@NotNull CommandListener listener);

    Collection<DiscordCommand> getCommands();

    @Nullable
    DiscordCommand getCommand(@NotNull String name);

    default CommandManager addCommand(@NotNull DiscordCommand command) {
        return addCommands(command);
    }

    CommandManager addCommands(@NotNull DiscordCommand... command);

    CommandManager removeCommand(@NotNull String name);

    List<CommandData> getCommandData();

    @Nullable
    CommandData getCommandData(@NotNull String name);

    void commitCommands(@NotNull JDA jda);
}
