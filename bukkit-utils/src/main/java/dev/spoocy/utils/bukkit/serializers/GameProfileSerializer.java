package dev.spoocy.utils.bukkit.serializers;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

    public static final GameProfileSerializer INSTANCE = new GameProfileSerializer();

    private GameProfileSerializer() { }

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
