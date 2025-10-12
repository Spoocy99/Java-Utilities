package dev.spoocy.jda.commands;

import dev.spoocy.jda.commands.permission.CommandPermission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AdminCommand extends DiscordCommand {

    protected AdminCommand(@NotNull String name, @NotNull String description, @NotNull InteractionContextType... context) {
        super(name, description, context, new CommandPermission[]{CommandPermission.ADMIN});
    }

}
