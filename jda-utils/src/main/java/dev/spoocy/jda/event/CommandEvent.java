package dev.spoocy.jda.event;

import dev.spoocy.jda.commands.arguments.ProvidedArgument;
import dev.spoocy.jda.commands.CommandManager;
import dev.spoocy.jda.message.MessageReply;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface CommandEvent extends MessageReply {

    @NotNull
    String getFullCommand();

    @NotNull
    String getCommand();

    @Nullable
    String getSubCommand();

    @NotNull
    List<? extends ProvidedArgument> getArguments();

    @Nullable
    ProvidedArgument getArgument(@NotNull String name);

    @NotNull
    CommandManager getCommandManager();

    @NotNull
    JDA getJDA();

    @Nullable
	default ShardManager getShardManager() {
		return getJDA().getShardManager();
	}

	@NotNull
	default SelfUser getSelfUser() {
		return getJDA().getSelfUser();
	}

    @NotNull
    User getUser();

    boolean isGuild();

    boolean isPrivate();

    @NotNull
    Member getMember();

    @NotNull
    Guild getGuild();

	@NotNull
	default Member getSelfMember() {
		return getGuild().getSelfMember();
	}

    default boolean hasPermission(@NotNull Permission... permission) {
		return getMember().hasPermission(permission);
	}

	default boolean hasChannelPermission(@NotNull Permission... permissions) {
		return getMember().hasPermission((GuildChannel) getChannel(), permissions);
	}

    @NotNull
    MessageChannel getChannel();

    default ChannelType getChannelType() {
		return getChannel().getType();
	}

    @NotNull
    TextChannel getTextChannel();

    PrivateChannel getPrivateChannel();

    boolean isInteraction();

    @NotNull
    Interaction getInteraction();

}
