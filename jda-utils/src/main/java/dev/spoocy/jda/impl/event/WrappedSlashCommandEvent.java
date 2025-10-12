package dev.spoocy.jda.impl.event;

import dev.spoocy.jda.commands.CommandManager;
import dev.spoocy.jda.commands.arguments.ProvidedArgument;
import dev.spoocy.jda.commands.arguments.WrappedOption;
import dev.spoocy.jda.event.AbstractCommandEvent;
import dev.spoocy.jda.message.ReplyAction;
import dev.spoocy.utils.common.collections.Collector;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class WrappedSlashCommandEvent extends AbstractCommandEvent {

    private final SlashCommandInteractionEvent event;
    private final List<WrappedOption> arguments;

    public WrappedSlashCommandEvent(@NotNull CommandManager manager, @NotNull SlashCommandInteractionEvent event) {
        super(manager, event.getChannel());
        this.event = event;
        this.arguments = WrappedOption.wrap(event.getOptions());
    }

    @NotNull
    @Override
    public String getFullCommand() {
        return event.getFullCommandName();
    }

    @NotNull
    @Override
    public String getCommand() {
        return event.getCommandString();
    }

    @Nullable
    @Override
    public String getSubCommand() {
        return event.getSubcommandName();
    }

    @NotNull
    @Override
    public List<? extends ProvidedArgument> getArguments() {
        return arguments;
    }

    @Nullable
    @Override
    public ProvidedArgument getArgument(@NotNull String name) {
        return Collector.of(arguments).first(a -> a.getName().equals(name)).orElse(null);
    }

    @Override
    public @NotNull User getUser() {
        return event.getUser();
    }

    @Override
    public boolean isGuild() {
        return event.isFromGuild();
    }

    @Override
    public boolean isPrivate() {
        return event.getChannel().getType().equals(ChannelType.PRIVATE);
    }

    @NotNull
    @Override
    public Member getMember() {
        this.checkGuild();
        return event.getMember();
    }

    @NotNull
    @Override
    public Guild getGuild() {
        this.checkGuild();
        return event.getGuild();
    }

    @NotNull
    @Override
    public TextChannel getTextChannel() {
        return event.getChannel().asTextChannel();
    }

    @Override
    public PrivateChannel getPrivateChannel() {
        return getUser().openPrivateChannel().complete();
    }

    @Override
    public boolean isInteraction() {
        return true;
    }

    @Override
    public @NotNull Interaction getInteraction() {
        return event.getInteraction();
    }

    @Override
    public ReplyAction reply(@NotNull String message) {
        return wrap(event.getHook().sendMessage(message));
    }

    @Override
    public ReplyAction reply(@NotNull Message message) {
        return wrap(event.getHook().sendMessage(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction reply(@NotNull MessageEmbed embed) {
        return wrap(event.getHook().sendMessage(new MessageCreateBuilder().addEmbeds(embed).build()));
    }

    @Override
    public ReplyAction send(@NotNull String message) {
        return wrap(event.getHook().sendMessage(message));
    }

    @Override
    public ReplyAction send(@NotNull Message message) {
        return wrap(event.getHook().sendMessage(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction send(@NotNull MessageEmbed embed) {
        return wrap(event.getHook().sendMessage(new MessageCreateBuilder().addEmbeds(embed).build()));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull String message) {
        return wrap(getPrivateChannel().sendMessage(message));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull Message message) {
        return wrap(getPrivateChannel().sendMessage(new MessageCreateBuilder().applyMessage(message).build()));
    }

    @Override
    public ReplyAction sendPrivate(@NotNull MessageEmbed embed) {
        return wrap(getPrivateChannel().sendMessage(new MessageCreateBuilder().addEmbeds(embed).build()));
    }
}
