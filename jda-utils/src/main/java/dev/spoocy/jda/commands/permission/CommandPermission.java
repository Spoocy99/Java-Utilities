package dev.spoocy.jda.commands.permission;

import dev.spoocy.jda.core.DiscordBot;
import dev.spoocy.jda.event.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandPermission {

    CommandPermission ADMIN = event -> event.hasPermission(Permission.ADMINISTRATOR);
    CommandPermission OWNER = event -> DiscordBot.getInstance().isOwner(event.getUser().getIdLong());

    boolean isCovered(@NotNull CommandEvent event);

}
