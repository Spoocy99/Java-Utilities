package dev.spoocy.utils.bukkit.serializers;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class GameProfileSerializer implements Serializer<GameProfile>, JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

    public static final GameProfileSerializer INSTANCE = new GameProfileSerializer();

    private GameProfileSerializer() { }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull GameProfile object) {
        Map<String, Object> result = new HashMap<>();

        if (object.getId() != null) {
            result.put("id", object.getId());
        }

        if (object.getName() != null) {
            result.put("name", object.getName());
        }

        if (!object.getProperties().isEmpty()) {
            Map<String, Object> properties = new HashMap<>();
            for (Map.Entry<String, Property> entry : object.getProperties().entries()) {
                properties.put(entry.getKey(), entry.getValue().getValue());
            }
            result.put("properties", properties);
        }

        return result;
    }

    @Override
    public @NotNull GameProfile deserialize(@NotNull Map<String, Object> map) {
        UUID id = (UUID) map.get("id");
        String name = (String) map.get("name");
        GameProfile profile = new GameProfile(id, name);

        if (map.containsKey("properties")) {
            Map<String, Object> properties = null;

            try {
                properties = (Map<String, Object>) map.get("properties");
            } catch (IllegalArgumentException ignored) { }

            if(properties == null) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    profile.getProperties().put(entry.getKey(), new Property(entry.getKey(), (String) entry.getValue()));
                }
            }
        }

        return profile;
    }

    @Override
    public JsonElement serialize(GameProfile gameProfile, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

            if (gameProfile.getId() != null) {
                result.add("id", jsonSerializationContext.serialize(gameProfile.getId()));
            }

            if (gameProfile.getName() != null) {
                result.addProperty("name", gameProfile.getName());
            }

            if (!gameProfile.getProperties().isEmpty()) {
                result.add("properties", jsonSerializationContext.serialize(gameProfile.getProperties()));
            }
            return result;
    }

    @Override
    public GameProfile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = (JsonObject) jsonElement;
        UUID id = object.has("id") ? (UUID) jsonDeserializationContext.deserialize(object.get("id"), UUID.class) : null;
        String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;

        GameProfile profile = new GameProfile(id, name);

        if (object.has("properties")) {
            for (Map.Entry<String, Property> prop : ((PropertyMap) jsonDeserializationContext.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                profile.getProperties().put(prop.getKey(), prop.getValue());
            }
        }

        return profile;
    }
}
