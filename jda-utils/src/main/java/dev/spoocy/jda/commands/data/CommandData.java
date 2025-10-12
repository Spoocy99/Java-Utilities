package dev.spoocy.jda.commands.data;

import dev.spoocy.jda.commands.DiscordCommand;
import dev.spoocy.utils.common.collections.Collector;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public class CommandData {

    private final DiscordCommand instance;
    private final String name;
    private final String description;
    private final InteractionContextType[] context;
    private final List<SubCommandData> subCommands;

    public CommandData(@NotNull DiscordCommand command) {
        this.instance = command;
        this.name = command.getName();
        this.description = command.getDescription();
        this.context = command.getContext();
        this.subCommands = new ArrayList<>();
    }

    public CommandData addSubCommandData(@NotNull SubCommandData subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    @Nullable
    public SubCommandData getSubCommandData(@NotNull String name) {
        return Collector.of(subCommands).filter(subCommandData -> subCommandData.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "CommandData{" +
                "description='" + description + '\'' +
                ", instance=" + instance +
                ", name='" + name + '\'' +
                ", type=" + Arrays.toString(context) +
                ", subCommands=" + subCommands +
                '}';
    }
}
