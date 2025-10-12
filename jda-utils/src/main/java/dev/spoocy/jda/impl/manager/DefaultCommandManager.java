package dev.spoocy.jda.impl.manager;

import dev.spoocy.jda.commands.*;
import dev.spoocy.jda.commands.annotations.Argument;
import dev.spoocy.jda.commands.annotations.Arguments;
import dev.spoocy.jda.commands.annotations.Command;
import dev.spoocy.jda.commands.data.CommandArgument;
import dev.spoocy.jda.commands.data.CommandData;
import dev.spoocy.jda.commands.data.CooldownData;
import dev.spoocy.jda.commands.data.SubCommandData;
import dev.spoocy.jda.commands.permission.CommandPermission;
import dev.spoocy.jda.commands.permission.DiscordPermission;
import dev.spoocy.jda.core.DiscordBot;
import dev.spoocy.jda.event.CommandEvent;
import dev.spoocy.jda.event.CommandPreProcessEvent;
import dev.spoocy.jda.impl.event.WrappedSlashCommandEvent;
import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import dev.spoocy.utils.common.scheduler.Scheduler;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultCommandManager implements CommandManager {

    private final ILogger LOGGER = ILogger.forThisClass();

    private final HashMap<String, CommandData> commands;
    private CommandListener listener;

    public DefaultCommandManager() {
        this.commands = new HashMap<>();
        this.listener = new CommandListener() {};
    }

    @Override
    public CommandListener getListener() {
        return this.listener;
    }

    @Override
    public CommandManager setListener(@NotNull CommandListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Collection<DiscordCommand> getCommands() {
        return Collector.of(commands.values()).map(CommandData::getInstance).asList();
    }

    @Override
    public DiscordCommand getCommand(@NotNull String name) {
        for(CommandData info : getCommandData()) {
            if(info.getName().equalsIgnoreCase(name)) return info.getInstance();
        }
        return null;
    }

    @Override
    public CommandManager addCommands(@NotNull DiscordCommand... command) {
        for(DiscordCommand c : command) {
            CommandData info = createCommandData(c);
            this.commands.put(info.getName(), info);
            LOGGER.debug("Added command '{}' ({} Commands)", info, this.commands.size());
        }
        return this;
    }

    @Override
    public CommandManager removeCommand(@NotNull String name) {
        this.commands.remove(name);
        LOGGER.debug("Removed command '{}'", name);
        return this;
    }

    @Override
    public List<CommandData> getCommandData() {
        return new ArrayList<>(this.commands.values());
    }

    @Override
    public CommandData getCommandData(@NotNull String name) {
        return this.commands.get(name);
    }

    @Override
    public void commitCommands(@NotNull JDA jda) {
        LOGGER.debug("Registered Commands: {}", this.commands.size());
        List<CommandData> data = getCommandData();
        CommandListUpdateAction commands = jda.updateCommands();

        for(CommandData info : data) {
            commands = commands.addCommands(createSlashCommandData(info));
        }
        commands.queue();
        LOGGER.debug("Commited {} commands on shard {} ({})" + data.size(), jda.getShardInfo().getShardId(), this.commands.size());
    }

    @NotNull
    public CommandData createCommandData(@NotNull DiscordCommand command) {
        CommandData data = new CommandData(command);

        for(MethodAccessor m : CommandResolver.getCommandMethods(command.getClass())) {

            Command cmd = m.getAnnotation(Command.class);

            List<CommandArgument> arguments = new ArrayList<>();

            if (m.hasAnnotation(Argument.class)) {
                arguments.add(CommandArgument.of(m.getAnnotation(Argument.class)));
            }

            if (m.hasAnnotation(Arguments.class)) {
                for (Argument arg : m.getAnnotation(Arguments.class).value()) {
                    arguments.add(CommandArgument.of(arg));
                }
            }

            SubCommandData subCommandInfo = new SubCommandData(
                    cmd.subCommand(),
                    cmd.description(),
                    DiscordPermission.mapPermission(cmd.permission()),
                    cmd.async(),
                    cmd.sendTyping(),
                    cmd.ephemeral(),
                    arguments,
                    m
            );

            data.addSubCommandData(subCommandInfo);
        }
        LOGGER.debug("Created CommandData for '{}': {}", command.getClass().getSimpleName(), data);
        return data;
    }

    @NotNull
    public SlashCommandData createSlashCommandData(@NotNull CommandData command) {
        SlashCommandData data = Commands.slash(command.getName(), command.getDescription());
        data.setContexts(command.getContext());

        for(SubCommandData subCommand : command.getSubCommands()) {

            String name = subCommand.getName();
            String description = subCommand.getDescription();

            if(StringUtils.isNullOrEmpty(name)) {

                for(CommandArgument argument : subCommand.getArguments()) {
                    data.addOption(argument.getType(), argument.getName(), argument.getDescription(), argument.isRequired());
                }

            } else {

                SubcommandData subData = new SubcommandData(name, description);
                for(CommandArgument argument : subCommand.getArguments()) {
                    subData.addOption(argument.getType(), argument.getName(), argument.getDescription(), argument.isRequired());
                }
                data.addSubcommands(subData);

            }

        }
        LOGGER.debug("Created SlashCommandData for '{}': {}", command.getName(), data);
        return data;
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String name = event.getName();
        CommandData data = getCommandData(name);
        if(data == null) return;

        String subCommandName = event.getSubcommandName() == null ? "" : event.getSubcommandName();
        SubCommandData subCommand = data.getSubCommandData(subCommandName);
        if(subCommand == null) return;

        event.deferReply(subCommand.isEphemeral()).queue();
        CommandEvent commandEvent = new WrappedSlashCommandEvent(this, event);

        if(data.getInstance() instanceof OwnerCommand
                && !DiscordBot.getInstance().isOwner(event.getUser().getIdLong())) {
            // let event timeout silently
            return;
        }

        CommandPermission[] permission = subCommand.getPermissions();

        User user = event.getUser();
        Member member = event.getMember();

        if(member != null && permission != null) {
            for(CommandPermission p : permission) {

                if(!p.isCovered(commandEvent)) {
                    Scheduler.runAsync(() -> this.listener.onNoPermissions(commandEvent))
                            .onException(e -> this.listener.onException(commandEvent, e));
                    return;
                }

            }
        }

        if(subCommand.hasCooldown()) {
            CooldownData cooldown = subCommand.getCooldown();

            if(cooldown.isOnCooldown(user, event.getGuild())) {
                Scheduler.runAsync(() -> this.listener.onCooldown(commandEvent))
                            .onException(e -> this.listener.onException(commandEvent, e));
                return;
            }

            cooldown.addCooldown(user, event.getGuild());
        }

        CommandPreProcessEvent preProcessEvent = new CommandPreProcessEvent(data, subCommand, commandEvent);

        this.listener.onPreProcess(preProcessEvent);

        if(preProcessEvent.isCancelled()) {
            return;
        }

        if(subCommand.isSendTyping()) {
            event.getChannel().sendTyping().queue();
        }

        if(subCommand.isAsync()) {
            Scheduler.runAsync(() -> subCommand.getMethod().invoke(data.getInstance(), commandEvent))
                    .onException(e -> this.listener.onException(commandEvent, e));
            return;
        }

        try {
            subCommand.getMethod().invoke(data.getInstance(), commandEvent);
        } catch (Exception e) {
            this.listener.onException(commandEvent, e);
        }
    }

}
