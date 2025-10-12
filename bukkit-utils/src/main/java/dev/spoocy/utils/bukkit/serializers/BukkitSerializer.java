package dev.spoocy.utils.bukkit.serializers;

import dev.spoocy.utils.config.serializer.Serializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BukkitSerializer<V extends ConfigurationSerializable> implements Serializer<V> {

    private final Class<V> clazz;

    public BukkitSerializer(@NotNull Class<V> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull V object) {
        return object.serialize();
    }

    @Override
    public @NotNull V deserialize(@NotNull Map<String, Object> map) {
        try {
            ConfigurationSerializable obj = ConfigurationSerialization.deserializeObject(map, clazz);
            if (obj == null) {
                throw new IllegalArgumentException("Deserialized object is null.");
            }
            return (V) obj;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to deserialize object: " + e.getMessage());
        }
    }
}
