package dev.spoocy.jda.commands.permission;

import dev.spoocy.jda.event.CommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class FuturePermission implements CommandPermission {

    private final Predicate<CommandEvent> permission;

    public FuturePermission(@NotNull Predicate<CommandEvent> permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "FuturePermission{" +
                "permission=" + permission +
                '}';
    }

    @Override
    public boolean isCovered(@NotNull CommandEvent event) {
        return permission.test(event);
    }
}
