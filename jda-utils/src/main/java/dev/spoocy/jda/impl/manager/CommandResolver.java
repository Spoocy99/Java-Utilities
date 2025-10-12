package dev.spoocy.jda.impl.manager;

import dev.spoocy.jda.commands.annotations.Command;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CommandResolver {

    public static Set<MethodAccessor> getCommandMethods(@NotNull Class<?> clazz) {
        return Reflection.builder()
                .forClass(clazz)
                .inheritedMembers()
                .buildAccess()
                .methodsWithAnnotation(Command.class);
    }

}
