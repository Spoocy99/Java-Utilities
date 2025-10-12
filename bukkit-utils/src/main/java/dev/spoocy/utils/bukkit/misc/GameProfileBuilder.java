package dev.spoocy.utils.bukkit.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import dev.spoocy.utils.bukkit.serializers.GameProfileSerializer;
import dev.spoocy.utils.common.cache.Cache;
import dev.spoocy.utils.common.cache.Caches;
import dev.spoocy.utils.common.log.ILogger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class GameProfileBuilder {

    private static final ILogger LOGGER = ILogger.forThisClass();
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(GameProfile.class, GameProfileSerializer.INSTANCE)
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .create();

    private static final Cache<UUID, CachedGameProfile> profiles = Caches.createCache();
    private static long CACHE_TIME = -1;

    public static void setCacheTime(long millis) {
        CACHE_TIME = millis;
    }

    public static GameProfile copy(@NotNull GameProfile profile) {
        GameProfile result = new GameProfile(profile.getId(), profile.getName());
        result.getProperties().putAll(profile.getProperties());
        return result;
    }

    public static GameProfile fetch(@NotNull UUID uuid) {
        return fetch(uuid, false);
    }

    private static final String SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public static GameProfile fetch(@NotNull UUID uuid, boolean force) {

        if(!force && profiles.contains(uuid)) {
            CachedGameProfile profile = profiles.get(uuid);

            if(profile != null && profile.isValid(CACHE_TIME)) {
                return profile.getProfile();
            }
        }

        String json = "";

        try {
            String url = String.format(SESSION_URL, fromUUID(uuid));
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            json = sb.toString();

        } catch (IOException e) {
            LOGGER.error("Failed to fetch GameProfile for UUID '{}'.", uuid, e);
        }

        if(json.isEmpty()) {
            LOGGER.error("GameProfile Profile for UUID '{}' not found.", uuid);
            return null;
        }

        GameProfile result = gson.fromJson(json, GameProfile.class);
        profiles.add(uuid, new CachedGameProfile(result));
        return result;
    }

    private static String fromUUID(final UUID value) {
        return value.toString().replace("-", "");
    }

    public static GameProfile getProfile(UUID uuid, String name, String skin) {
        return getProfile(uuid, name, skin, null);
    }

    private static final String JSON_SKIN = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String JSON_CAPE = "{\"timestamp\":%d,\"profileId\":\"%s\",\"profileName\":\"%s\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"%s\"},\"CAPE\":{\"url\":\"%s\"}}}";

    public static GameProfile getProfile(@NotNull UUID uuid, @Nullable String name, String skinUrl, @Nullable String capeUrl) {
        GameProfile profile = new GameProfile(uuid, name);
        boolean capePresent = capeUrl != null && !capeUrl.isEmpty();

        List<Object> args = new ArrayList<>();
        args.add(System.currentTimeMillis());
        args.add(UUIDTypeAdapter.fromUUID(uuid));
        args.add(name);
        args.add(skinUrl);
        if (capePresent) args.add(capeUrl);

        profile.getProperties().put("textures", new Property(
                "textures",
                Base64Coder.encodeString(
                        String.format(capePresent ? JSON_CAPE : JSON_SKIN, args.toArray(new Object[0]))
                )
        ));
        return profile;
    }

    private static class CachedGameProfile {

        private final long timestamp;
        @Getter
        private final GameProfile profile;

        public CachedGameProfile(GameProfile profile) {
            this.timestamp = System.currentTimeMillis();
            this.profile = profile;
        }

        public boolean isValid(long max) {
            return max < 0 || (System.currentTimeMillis() - timestamp) < max;
        }
    }

}
