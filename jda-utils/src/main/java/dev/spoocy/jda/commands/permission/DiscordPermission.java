package dev.spoocy.jda.commands.permission;

import dev.spoocy.jda.event.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DiscordPermission implements CommandPermission {

    public static DiscordPermission[] mapPermission(@NotNull Permission... permission) {
        DiscordPermission[] permissions = new DiscordPermission[permission.length];
        for (int i = 0; i < permission.length; i++) {
            permissions[i] = new DiscordPermission(permission[i]);
        }
        return permissions;
    }

    private final Permission permission;

    public DiscordPermission(@NotNull Permission permission) {
        this.permission = permission;
    }

    @Override
    public boolean isCovered(@NotNull CommandEvent event) {
        Member member = event.getMember();
        GuildChannel channel = event.getTextChannel();

        return member.hasPermission(channel, permission);
    }

    @Override
    public String toString() {
        return "DiscordPermission{" +
                "permission=" + permission +
                '}';
    }
}
