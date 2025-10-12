package dev.spoocy.jda.commands;

import dev.spoocy.jda.commands.permission.CommandPermission;
import dev.spoocy.jda.event.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Command that can only be executed by the bot owner(s).
 * No Error Reply is sent if a non-owner tries to execute this command.
 * <p>
 * If you want to send an Error Reply, use a normal {@link DiscordCommand} with the {@link CommandPermission#OWNER} permission.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public abstract class OwnerCommand extends DiscordCommand {

    protected OwnerCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType... context) {
        super(name, description, context);
    }

    protected OwnerCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType[] context, @NotNull CommandPermission[] permissions) {
        super(name, description, context, permissions);
    }

    protected OwnerCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType[] context, @NotNull Permission... permissions) {
        super(name, description, context, permissions);
    }

    protected OwnerCommand(@NotNull String name, @NotNull String description, @NotNull Predicate<CommandEvent> permission, @NotNull InteractionContextType... context) {
        super(name, description, permission, context);
    }
}
