package dev.spoocy.jda.commands;

import dev.spoocy.jda.event.CommandEvent;
import dev.spoocy.jda.event.CommandPreProcessEvent;
import dev.spoocy.utils.common.log.ILogger;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandListener {

    default void onPreProcess(@NotNull CommandPreProcessEvent event) { }

    default void onUnknownCommand(@NotNull CommandEvent event) {
        event.reply(new EmbedBuilder().setDescription("Unknown Command.").setColor(Color.RED)).queue();
    }

    default void onNoPermissions(@NotNull CommandEvent event) {
        event.reply(new EmbedBuilder().setDescription("You don't have permission to use this command.").setColor(Color.RED)).queue();
    }

    default void onCooldown(@NotNull CommandEvent event) {
        event.reply(new EmbedBuilder().setDescription("Please wait a bit before executing this command again.").setColor(Color.RED)).queue();
    }

    default void onException(@NotNull CommandEvent event, Throwable error) {
        event.reply(new EmbedBuilder().setDescription("An error occurred while executing the command. Please try again later.").setColor(Color.RED)).queue();
        ILogger.forThisClass().error("An error occurred while executing the command: " + event.getCommand(), error);
    }

}
