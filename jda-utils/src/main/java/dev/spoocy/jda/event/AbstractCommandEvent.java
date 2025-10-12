package dev.spoocy.jda.event;

import dev.spoocy.jda.commands.CommandManager;
import dev.spoocy.jda.impl.actions.WrappedMessageReplyAction;
import dev.spoocy.jda.impl.actions.WrappedWebhookReplyAction;
import dev.spoocy.jda.message.ReplyAction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractCommandEvent implements CommandEvent {

    protected final CommandManager manager;
    protected final MessageChannel channel;

    protected AbstractCommandEvent(@NotNull CommandManager manager, @NotNull MessageChannel channel) {
        this.manager = manager;
        this.channel = channel;
    }

    protected void checkGuild() {
        if(!isGuild()) throw new IllegalStateException("Command is not called in a guild!");
    }

    protected void checkPrivate() {
        if(!isPrivate()) throw new IllegalStateException("Command is not called in a private channel!");
    }

    protected void checkInteraction() {
        if(!isInteraction()) throw new IllegalStateException("Command is not called as an interaction!");
    }

	protected ReplyAction wrap(@NotNull MessageCreateAction action) {
		return new WrappedMessageReplyAction(action);
	}

	protected ReplyAction wrap(@NotNull WebhookMessageCreateAction<Message> action) {
		return new WrappedWebhookReplyAction(action);
	}

    @Override
    public @NotNull CommandManager getCommandManager() {
        return this.manager;
    }

    @Override
    public @NotNull JDA getJDA() {
        return this.channel.getJDA();
    }

    @Override
    @NotNull
    public MessageChannel getChannel() {
        return channel;
    }
}
