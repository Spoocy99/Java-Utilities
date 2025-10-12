package dev.spoocy.jda.commands.data;

import dev.spoocy.jda.commands.annotations.Argument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
@AllArgsConstructor
public class CommandArgument {

    public static CommandArgument of(Argument argument) {
        return new CommandArgument(argument.type(), argument.name(), argument.description(), argument.required());
    }

    private final OptionType type;
    private final String name;
    private final String description;
    private final boolean required;

}
