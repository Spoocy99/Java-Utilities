package dev.spoocy.jda.commands.data;

import dev.spoocy.jda.commands.permission.CommandPermission;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public class SubCommandData {

    private final String name;
    private final String description;
    private final CommandPermission[] permissions;
    private final boolean async;
    private final boolean sendTyping;
    private final boolean ephemeral;

    private final List<CommandArgument> arguments;
    private final MethodAccessor method;

    private CooldownData cooldown;

    public SubCommandData(@NotNull String name,
                          @NotNull String description,
                          @NotNull CommandPermission[] permissions,
                          boolean async,
                          boolean sendTyping,
                          boolean ephemeral,
                          @NotNull List<CommandArgument> arguments,
                          @NotNull MethodAccessor method,
                          @NotNull CooldownData cooldown
    ) {
        this(name, description, permissions, async, sendTyping, ephemeral, arguments, method);
        this.cooldown = cooldown;
    }

     public SubCommandData(@NotNull String name,
                           @NotNull String description,
                           @NotNull CommandPermission[] permissions,
                           boolean async,
                           boolean sendTyping,
                           boolean ephemeral,
                           @NotNull List<CommandArgument> arguments,
                           @NotNull MethodAccessor method

     ) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.async = async;
        this.sendTyping = sendTyping;
        this.ephemeral = ephemeral;
        this.arguments = arguments;
        this.method = method;
     }

     public boolean hasCooldown() {
         return this.cooldown != null;
     }

    @Override
    public String toString() {
        return "SubCommandData{" +
                "arguments=" + arguments.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", permissions=" + Arrays.toString(permissions) +
                ", async=" + async +
                ", sendTyping=" + sendTyping +
                ", method=" + method +
                ", cooldown=" + cooldown +
                '}';
    }
}
