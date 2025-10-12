package dev.spoocy.utils.bukkit.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BukkitUtils {

    public static String getPrettyName(@NotNull Material material) {
        return getPrettyName(material.name());
    }

    public static String getPrettyName(@Nullable String name) {
        if (name == null) return "";
        StringBuilder builder = new StringBuilder();
        String[] chars = name.split("");

        // first should be upper case
        boolean upper_case = true;
        for (String c : chars) {

            // new word
            if (c.equals("_")) {
                upper_case = true;
                builder.append(" ");
                continue;
            }

            // char should be upper case
            if (upper_case) {
                builder.append(c.toUpperCase());
                upper_case = false;
                continue;
            }

            // char is lower case
            builder.append(c.toLowerCase());

        }

        // clean up first char
        return builder.toString().replace("And", "and");
    }

    public static boolean isLegacy(@NotNull Material material) {
        return material.name().contains("LEGACY");
    }

    public static boolean isAir(@NotNull Material material) {
		try {
            return material.isAir();
        } catch (Throwable ignored) { }

		switch (material.name()) {
			case "AIR":
			case "VOID_AIR":
			case "CAVE_AIR":
				return true;
		}

        return false;
	}

    public static boolean isLiquid(@NotNull Material material) {
        String name = material.name().toUpperCase();
        switch (name) {
            case "WATER":
            case "LEGACY_WATER":
            case "STATIONARY_WATER":
            case "LEGACY_STATIONARY_WATER":
            case "LAVA":
            case "LEGACY_LAVA":
            case "STATIONARY_LAVA":
            case "LEGACY_STATIONARY_LAVA":
                return true;
        }

        return false;
    }

    public static boolean isHalfBlock(@NotNull Material material) {
        String name = material.name().toUpperCase();

        if (name.contains("SLAB")
                || name.contains("STEP")
                || name.contains("STAIRS")
        ) { return true; }

        switch (name) {
            case "SNOW":
            case "FARMLAND":
            case "SOUL_SAND":
            case "CACTUS":

            case "ENCHANTING_TABLE":

            case "CHEST":
            case "TRAPPED_CHEST":
            case "ENDER_CHEST":

            case "BREWING_STAND":
            case "LANTERN":
            case "CAMPFIRE":
                return true;
        }

        return false;
    }

    public static boolean isPhysicalBlock(@Nullable Material material) {
        if (material == null) return false;

        if (isAir(material)
                || !material.isSolid()
                || !material.isBlock()
        ) return false;

        return true;
    }

    public static boolean isItem(@Nullable Material material) {
        if (material == null) return false;

        if (isAir(material)
                || !material.isItem()
        ) return false;

        return true;
    }

    public static boolean isObtainable(@Nullable Material material) {
        if (material == null) return false;
        if (isAir(material)) return false;

        String name = material.name().toUpperCase();

        if (name.contains("SPAWN_EGG")
                || name.contains("LEGACY")
                || name.contains("PATTERN")
                || name.contains("INFESTED")
                || name.contains("COMMAND_BLOCK")
                || name.contains("PETRIFIED")
                || name.equals("SCULK_SENSOR")
        ) return false;

        switch (name) {
            case "PLAYER_HEAD":
            case "BEDROCK":
            case "BARRIER":
            case "STRUCTURE_BLOCK":
            case "STRUCTURE_VOID":
            case "JIGSAW":
            case "SPAWNER":
            case "END_PORTAL":
            case "END_PORTAL_FRAME":
            case "END_GATEWAY":
            case "PORTAL":
            case "NETHER_PORTAL":
            case "LAVA":
            case "WATER":
            case "POWDER_SNOW":
            case "TALL_GRASS":
            case "TALL_SEAGRASS":
            case "LARGE_FERN":
            case "CHORUS_FLOWER":
            case "CHORUS_PLANT":
            case "FARMLAND":
            case "AMETHYST_CLUSTER":
            case "BUDDING_AMETHYST":
            case "LIGHT":
            case "BUNDLE":
            case "FROGSPAWN":
            case "ECHO_SHARD":
            case "GLOBE_BANNER_PATTERN":
            case "DEBUG_STICK":
            case "DIRT_PATH":
            case "KNOWLEDGE_BOOK":
                return false;
        }

        return true;
    }

    public static boolean isFromEnd(@NotNull Material material) {
		String name = material.name();
		return name.contains("END")
                || name.contains("ELYTRA")
				|| name.contains("SHULKER")
				|| name.contains("PURPUR")
                ;
	}

	public static boolean isSpecialType(@NotNull Material material) {
		String name = material.name();
		return name.contains("EXPOSED") ||
				name.contains("WEATHERED") ||
				name.contains("OXIDIZED") ||
				name.contains("BUD");
	}

    public static boolean isPortal(@NotNull Material material) {
        String name = material.name().toUpperCase();

        switch (name) {
            case "END_PORTAL":
            case "END_PORTAL_FRAME":
            case "END_GATEWAY":
            case "NETHER_PORTAL":
                return true;
            default:
                return false;
        }
    }

    public static boolean isSlab(@NotNull Material material) {
        return material.name().toUpperCase().contains("SLAB");
    }

    public static int getMinHeight(@NotNull World world) {
		try {
			return world.getMinHeight();
		} catch (Throwable ignored) { }
		return 0;
	}

    public static int getMaxHeight(@NotNull World world) {
        try {
            return world.getMaxHeight();
        } catch (Throwable ignored) { }
        return 256;
    }

    public static boolean isSameBlock(@Nullable Location loc1, @Nullable Location loc2) {
        return isSameBlock(loc1, loc2, false);
    }

    public static boolean isSameBlock(@Nullable Location loc1, @Nullable Location loc2, boolean ignoreHeight) {
		if (loc1 == null || loc2 == null) return true;
		if (loc1.getWorld() != loc2.getWorld()
                || loc1.getBlockX() != loc2.getBlockX()
                || loc1.getBlockZ() != loc2.getBlockZ()
        ) return false;
        return ignoreHeight || loc1.getBlockY() == loc2.getBlockY();
    }

    public static boolean isSameBlock(@Nullable Block b1, @Nullable Block b2) {
        if (b1 == null || b2 == null) return false;
		return isSameBlock(b1.getLocation(), b2.getLocation());
	}

    public static boolean isSameChunk(@Nullable Chunk c1, @Nullable Chunk c2) {
        if (c1 == null || c2 == null) return false;
        return c1.getX() == c2.getX() && c1.getZ() == c2.getZ();
    }

    public static boolean isDamageable(@NotNull Material material) {
        return material.getMaxDurability() > 0;
    }

    public static boolean isDamageable(@NotNull ItemStack item) {
        return item.getItemMeta() instanceof Damageable;
    }

}
