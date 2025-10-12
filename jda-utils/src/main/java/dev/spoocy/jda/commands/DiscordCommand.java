package dev.spoocy.jda.commands;

import dev.spoocy.jda.commands.permission.CommandPermission;
import dev.spoocy.jda.commands.permission.DiscordPermission;
import dev.spoocy.jda.commands.permission.FuturePermission;
import dev.spoocy.jda.event.CommandEvent;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public abstract class DiscordCommand {

    private final String name;
    private final String description;
    private final InteractionContextType[] context;
    private final CommandPermission[] permissions;

    protected DiscordCommand(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull InteractionContextType[] context,
            final @NotNull CommandPermission[] permissions
    ) {
        this.name = name;
        this.description = description;
        this.context = context;
        this.permissions = permissions;
    }

    protected DiscordCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType... context) {
        this(name, description, context, new CommandPermission[0]);
    }

    protected DiscordCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType[] context, @NotNull Permission... permissions) {
        this(name, description, context, DiscordPermission.mapPermission(permissions));
    }

    protected DiscordCommand(@NotNull String name, @NotNull String description, @NotNull Predicate<CommandEvent> permission, @NotNull InteractionContextType... context) {
        this(name, description, context, new CommandPermission[]{new FuturePermission(permission)});
    }
}
