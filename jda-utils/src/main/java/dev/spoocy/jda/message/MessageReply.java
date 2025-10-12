package dev.spoocy.jda.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface MessageReply {

    ReplyAction reply(@NotNull String message);

	ReplyAction reply(@NotNull Message message);

    ReplyAction reply(@NotNull MessageEmbed embed);

    default ReplyAction reply(@NotNull EmbedBuilder builder) {
        return reply(builder.build());
    }

    ReplyAction send(@NotNull String message);

    ReplyAction send(@NotNull Message message);

    ReplyAction send(@NotNull MessageEmbed embed);

    default ReplyAction send(@NotNull EmbedBuilder builder) {
        return send(builder.build());
    }

    ReplyAction sendPrivate(@NotNull String message);

    ReplyAction sendPrivate(@NotNull Message message);

    ReplyAction sendPrivate(@NotNull MessageEmbed embed);

    default ReplyAction sendPrivate(@NotNull EmbedBuilder builder) {
        return sendPrivate(builder.build());
    }
}
