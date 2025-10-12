package dev.spoocy.jda.commands.data;

import dev.spoocy.jda.commands.annotations.Cooldown;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class CooldownData {

    private final Cooldown.Scope scope;
    private final long cooldown;
    private final TimeUnit unit;
    private final HashMap<Long, Long> cooldowns;

    public CooldownData(Cooldown.Scope scope, long cooldown, TimeUnit unit) {
        this.scope = scope;
        this.cooldown = cooldown;
        this.unit = unit;
        this.cooldowns = new HashMap<>();
    }

    public Cooldown.Scope getCooldownScope() {
        return this.scope;
    }

    public TimeUnit getTimeUnit() {
        return this.unit;
    }

    public long getDefaultCooldown() {
        return this.cooldown;
    }

    public void clearCooldowns() {
        this.cooldowns.clear();
    }

    public boolean isOnCooldown(@NotNull User user, @Nullable Guild guild) {
        long id = this.scope == Cooldown.Scope.GLOBAL ? 0L : this.scope == Cooldown.Scope.GUILD ? guild.getIdLong() : user.getIdLong();
        return this.cooldowns.containsKey(id) && this.getRemainingCooldown(user, guild) > 0;
    }

    public void addCooldown(@NotNull User user, @Nullable Guild guild) {
        long id = this.scope == Cooldown.Scope.GLOBAL ? 0L : this.scope == Cooldown.Scope.GUILD ? guild.getIdLong() : user.getIdLong();
        this.cooldowns.put(id, System.currentTimeMillis() + this.unit.toMillis(this.cooldown));
    }

    public void removeCooldown(@NotNull User user, @Nullable Guild guild) {
        long id = this.scope == Cooldown.Scope.GLOBAL ? 0L : this.scope == Cooldown.Scope.GUILD ? guild.getIdLong() : user.getIdLong();
        this.cooldowns.remove(id);
    }

    public long getCooldownTimeMillies(@NotNull User user, @Nullable Guild guild) {
        long id = this.scope == Cooldown.Scope.GLOBAL ? 0L : this.scope == Cooldown.Scope.GUILD ? guild.getIdLong() : user.getIdLong();
        return this.cooldowns.getOrDefault(id, -1L);
    }

    public long getRemainingCooldown(@NotNull User user, @Nullable Guild guild) {
        long id = this.scope == Cooldown.Scope.GLOBAL ? 0L : this.scope == Cooldown.Scope.GUILD ? guild.getIdLong() : user.getIdLong();
        long time = this.cooldowns.getOrDefault(id, -1L);
        return time == -1 ? -1 : time - System.currentTimeMillis();
    }

}
