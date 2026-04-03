package dev.spoocy.utils.bukkit.serializers;

import com.mojang.authlib.GameProfile;
import dev.spoocy.utils.config.serializer.ConfigSerializable;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.serializer.Serializer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BukkitSerializers extends NamedSerializers {

    private static final BukkitSerializers INSTANCE = new BukkitSerializers();

    private final Map<Class<?>, Serializer<?>> assignable = new ConcurrentHashMap<>();

    private BukkitSerializers() {
        super();
        this.register(GameProfile.class, GameProfileSerializer.INSTANCE);
        this.registerAssignable(Inventory.class, InventorySerializer.INSTANCE);
        this.registerBukkit(Vector.class);
        this.registerBukkit(BlockVector.class);
        this.registerBukkit(ItemStack.class);
        this.registerBukkit(Color.class);
        this.registerBukkit(PotionEffect.class);
        this.registerBukkit(FireworkEffect.class);
        this.registerBukkit(Pattern.class);
        this.registerBukkit(Location.class);
        try {
            this.registerBukkit(AttributeModifier.class);
        } catch (Throwable ignored) {}
        try {
            this.registerBukkit(BoundingBox.class);
        } catch (Throwable ignored) {}
        try {
            this.registerBukkit(SpawnRule.class);
        } catch (Throwable ignored) {}
    }

    public static @NotNull BukkitSerializers getInstance() {
        return INSTANCE;
    }

    public static @Nullable <T> Serializer<T> resolveSerializer(@NotNull Class<T> clazz) {
        return INSTANCE.resolve(clazz);
    }

    public static void registerConfigSerializable(@NotNull Class<? extends ConfigSerializable> clazz) {
        INSTANCE.register(clazz);
    }

    public static void registerBukkitSerializer(@NotNull Class<? extends ConfigurationSerializable> clazz) {
        INSTANCE.registerBukkit(clazz);
    }

    public static <T> void registerAssignableSerializer(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer) {
        INSTANCE.registerAssignable(clazz, serializer);
    }

    @Override
    public void register(@NotNull Class<? extends ConfigSerializable> clazz) {
        super.register(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> Serializer<T> resolve(@NotNull Class<T> clazz) {
        Serializer<T> serializer = super.resolve(clazz);
        if (serializer != null) return serializer;

        Class<?> matchedType = null;
        Serializer<?> matchedSerializer = null;

        for (Map.Entry<Class<?>, Serializer<?>> entry : this.assignable.entrySet()) {
            Class<?> assignableType = entry.getKey();
            if (!assignableType.isAssignableFrom(clazz)) {
                continue;
            }

            // Prefer the most specific assignable type when multiple serializers match.
            if (matchedType == null || matchedType.isAssignableFrom(assignableType)) {
                matchedType = assignableType;
                matchedSerializer = entry.getValue();
            }
        }

        return (Serializer<T>) matchedSerializer;

    }

    public <T extends ConfigurationSerializable> void registerBukkit(@NotNull Class<T> clazz) {
        String name = getBukkitName(clazz);
        this.registerName(clazz, name);
        this.register(clazz, new BukkitSerializer<>(clazz));
    }

    public <T> void registerAssignable(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer) {
        this.assignable.put(clazz, serializer);
    }

    private <T extends ConfigurationSerializable> String getBukkitName(@NotNull Class<T> clazz) {
        return ConfigurationSerialization.getAlias(clazz);
    }

}
