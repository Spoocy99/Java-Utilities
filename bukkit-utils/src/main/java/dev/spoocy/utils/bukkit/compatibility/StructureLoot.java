package dev.spoocy.utils.bukkit.compatibility;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a loot table for each structure.
 * Based on https://minecraft.fandom.com/wiki/Structure
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class StructureLoot {

    private static final HashMap<String, StructureLoot> BY_KEY = new HashMap<>();

    public static final StructureLoot

    ANCIENT_CITY = new StructureLoot(new String[]{"chests/ancient_city", "chests/ancient_city_ice_box", "chests/ancient_city_box"},
            "COAL", "BONE", "SOUL_TORCH", "BOOK", "POTION", "ENCHANTED_BOOK", "DISC_FRAGMENT_5", "AMETHYST_SHARD", "GLOW_BERRIES", "SCULK",
            "CANDLE", "IRON_LEGGINGS", "SCULK_CATALYST", "COMPASS", "MUSIC_DISC_13", "MUSIC_DISC_CAT", "LEAD", "NAME_TAG", "SADDLE", "DIAMOND_HOE",
            "DIAMOND_HORSE_ARMOR", "DIAMOND_LEGGINGS", "ENCHANTED_GOLDEN_APPLE"),

    MINESHAFT = new StructureLoot(new String[]{"chests/abandoned_mineshaft"},
            "RAIL", "TORCH", "NAME_TAG", "GLOW_BERRIES", "BREAD", "GOLDEN_APPLE", "COAL", "BEETROOT_SEEDS", "MELON_SEEDS", "PUMPKIN_SEEDS",
            "IRON_INGOT", "ACTIVATOR_RAIL", "DETECTOR_RAIL", "POWERED_RAIL", "LAPIS_LAZULI", "REDSTONE", "GOLD_INGOT", "ENCHANTED_BOOK", "DIAMOND",
            "IRON_PICKAXE", "ENCHANTED_GOLDEN_APPLE", "MUSIC_DISC_OTHERSIDE", "WARD_ARMOR_TRIM_SMITHING_TEMPLATE", "SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE"),

    STRONGHOLD = new StructureLoot(new String[]{"chests/stronghold_library", "chests/stronghold_corridor", "chests/stronghold_crossing"},
            "APPLE", "BREAD", "IRON_INGOT", "ENDER_PEARL", "REDSTONE", "GOLD_INGOT", "IRON_PICKAXE", "IRON_SWORD", "IRON_HELMET", "IRON_CHESTPLATE",
            "IRON_LEGGINGS", "IRON_BOOTS", "EYE_ARMOR_TRIM_SMITHING_TEMPLATE", "EMERALD", "DIAMOND", "ENCHANTED_BOOK", "MUSIC_DISC_OTHERSIDE", "SADDLE",
            "IRON_HORSE_ARMOR", "GOLDEN_APPLE", "GOLDEN_HORSE_ARMOR", "DIAMOND_HORSE_ARMOR"),

    BURIED_TREASURE = new StructureLoot(new String[]{"chests/buried_treasure"},
            "HEART_OF_THE_SEA", "IRON_INGOT", "CHAINMAIL_HELMET", "CHAINMAIL_CHESTPLATE", "CHAINMAIL_LEGGINGS", "CHAINMAIL_BOOTS", "POTION", "DIAMOND",
            "GOLD_INGOT", "LEAD", "TNT", "NAME_TAG", "PRISMARINE_SHARD", "WRITABLE_BOOK", "MUSIC_DISC_MELLOHI", "MUSIC_DISC_WAIT", "EXPERIENCE_BOTTLE", "CAKE"),

    TRAIL_RUINS_COMMON = new StructureLoot(new String[]{"archaeology/trail_ruins_common", "archaeology/trail_ruins_rare"},
            "BLUE_DYE", "BRICK", "BROWN_CANDLE", "EMERALD", "GREEN_CANDLE", "LIGHT_BLUE_DYE", "ORANGE_DYE", "PURPLE_CANDLE", "RED_CANDLE", "WHEAT", "WHITE_DYE", "WOODEN_HOE",
            "YELLOW_DYE", "BEETROOT_SEEDS", "BLUE_STAINED_GLASS_PANE", "COAL", "DEAD_BUSH", "FLOWER_POT", "LEAD", "LIGHT_BLUE_STAINED_GLASS_PANE", "BLUE_STAINED_GLASS_PANE",
            "OAK_HANGING_SIGN", "PINK_STAINED_GLASS_PANE", "PURPLE_STAINED_GLASS_PANE", "RED_STAINED_GLASS_PANE", "SPRUCE_HANGING_SIGN", "STRING", "WHEAT_SEEDS", "YELLOW_STAINED_GLASS_PANE",
            "GOLD_NUGGET"),

    DESERT_PYRAMID = new StructureLoot(new String[]{"chests/desert_pyramid", "archaeology/desert_pyramid"},
            "BONE", "ROTTEN_FLESH", "GUNPOWDER", "SAND", "STRING", "SPIDER_EYE", "ENCHANTED_BOOK", "SADDLE", "GOLDEN_APPLE", "GOLD_INGOT", "IRON_INGOT",
            "EMERALD", "IRON_HORSE_ARMOR", "DUNE_ARMOR_TRIM_SMITHING_TEMPLATE", "GOLDEN_HORSE_ARMOR", "DIAMOND", "DIAMOND_HORSE_ARMOR", "ENCHANTED_GOLDEN_APPLE"),

    IGLOO = new StructureLoot(new String[]{"chests/igloo_chest"},
            "GOLDEN_APPLE", "COAL", "APPLE", "WHEAT", "GOLD_NUGGET", "ROTTEN_FLESH", "STONE_AXE", "EMERALD"),

    JUNGLE_TEMPLE = new StructureLoot(new String[]{"chests/jungle_temple", "chests/jungle_temple_dispenser"},
            "BONE", "ROTTEN_FLESH", "GOLD_INGOT", "BAMBOO", "IRON_INGOT", "WILD_ARMOR_TRIM_SMITHING_TEMPLATE", "DIAMOND", "SADDLE", "EMERALD", "ENCHANTED_BOOK",
            "IRON_HORSE_ARMOR", "DIAMOND_HORSE_ARMOR", "ARROW"),

    PILLAGER_OUTPOST = new StructureLoot(new String[]{"chests/pillager_outpost"},
            "DARK_OAK_LOG", "WHEAT", "EXPERIENCE_BOTTLE", "CARROT", "POTATO", "CROSSBOW", "GOAT_HORN", "ARROW", "STRING", "TRIPWIRE_HOOK", "IRON_INGOT",
            "SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE", "ENCHANTED_BOOK"),

    WOODLAND_MANSION = new StructureLoot(new String[]{"chests/woodland_mansion"},
            "BONE", "GUNPOWDER", "ROTTEN_FLESH", "STRING", "VEX_ARMOR_TRIM_SMITHING_TEMPLATE", "WHEAT", "BREAD", "LEAD", "NAME_TAG", "COAL", "REDSTONE",
            "MUSIC_DISC_13", "MUSIC_DISC_CAT", "GOLDEN_APPLE", "DIAMOND_HOE", "BEETROOT_SEEDS", "MELON_SEEDS", "PUMPKIN_SEEDS", "IRON_INGOT", "BUCKET", "ENCHANTED_BOOK",
            "CHAINMAIL_CHESTPLATE", "GOLD_INGOT", "DIAMOND_CHESTPLATE", "ENCHANTED_GOLDEN_APPLE"),

    OCEAN_RUIN = new StructureLoot(new String[]{"archaeology/ocean_ruin_warm", "\"archaeology/ocean_ruin_cold"},
    "COAL", "WHEAT", "GOLD_NUGGET", "MAP", "ENCHANTED_BOOK", "FISHING_ROD", "EMERALD", "LEATHER_CHESTPLATE", "GOLDEN_APPLE", "GOLDEN_HELMET"),

    SHIPWRECK = new StructureLoot(new String[]{"chests/shipwreck_map", "chests/shipwreck_supply", "chests/shipwreck_treasure"},
            "SUSPICIOUS_STEW", "PAPER", "WHEAT", "CARROT", "POISONOUS_POTATO", "POTATO", "MOSS_BLOCK", "COAL", "ROTTEN_FLESH", "GUNPOWDER",
            "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS", "COAST_ARMOR_TRIM_SMITHING_TEMPLATE", "BAMBOO", "PUMPKIN", "TNT"),

    NETHER_FORTRESS = new StructureLoot(new String[]{"chests/nether_bridge"},
            "GOLD_INGOT", "SADDLE", "GOLDEN_HORSE_ARMOR", "NETHER_WART", "IRON_INGOT", "DIAMOND", "FLINT_AND_STEEL", "IRON_HORSE_ARMOR",
            "GOLDEN_SWORD", "GOLDEN_CHESTPLATE", "DIAMOND_HORSE_ARMOR", "OBSIDIAN", "RIB_ARMOR_TRIM_SMITHING_TEMPLATE"),

    BASTION = new StructureLoot(new String[]{"chests/bastion_treasure", "chests/bastion_other", "chests/bastion_bridge", "chests/bastion_hoglin_stable"},
            "LODESTONE", "ARROW", "IRON_NUGGET", "GOLD_NUGGET", "STRING", "LEATHER", "SPECTRAL_ARROW", "GILDED_BLACKSTONE", "IRON_INGOT",
            "GOLD_INGOT", "CRYING_OBSIDIAN", "CROSSBOW", "GOLD_BLOCK", "GOLDEN_SWORD", "GOLDEN_AXE", "GOLDEN_HELMET", "GOLDEN_CHESTPLATE",
            "GOLDEN_LEGGINGS", "GOLDEN_BOOTS", "NETHERITE_UPGRADE_SMITHING_TEMPLATE", "SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE"),

    RUINED_PORTAL = new StructureLoot(new String[]{"chests/ruined_portal"},
    "IRON_NUGGET", "FLINT", "OBSIDIAN", "FIRE_CHARGE", "FLINT_AND_STEEL", "GOLD_NUGGET", "GOLDEN_APPLE", "GOLDEN_AXE", "GOLDEN_HOE", "GOLDEN_PICKAXE",
            "GOLDEN_SHOVEL", "GOLDEN_SWORD", "GOLDEN_HELMET", "GOLDEN_CHESTPLATE", "GOLDEN_LEGGINGS", "GOLDEN_BOOTS", "GLISTERING_MELON_SLICE", "GOLDEN_CARROT",
            "GOLD_INGOT", "CLOCK", "LIGHT_WEIGHTED_PRESSURE_PLATE", "GOLDEN_HORSE_ARMOR", "GOLD_BLOCK", "BELL", "ENCHANTED_GOLDEN_APPLE"),

    END_CITY = new StructureLoot(new String[]{"chests/end_city_treasure"},
            "GOLD_INGOT", "IRON_INGOT", "BEETROOT_SEEDS", "DIAMOND", "SADDLE", "IRON_PICKAXE", "IRON_SHOVEL", "IRON_SWORD", "IRON_HELMET",
            "IRON_CHESTPLATE", "IRON_LEGGINGS", "IRON_BOOTS", "DIAMOND_PICKAXE", "DIAMOND_SHOVEL", "DIAMOND_SWORD", "DIAMOND_HELMET", "DIAMOND_CHESTPLATE",
            "DIAMOND_LEGGINGS", "DIAMOND_BOOTS", "EMERALD", "SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE", "IRON_HORSE_ARMOR", "GOLDEN_HORSE_ARMOR", "DIAMOND_HORSE_ARMOR"),

    DUNGEON = new StructureLoot(new String[]{"chests/simple_dungeon"},
            "BONE", "GUNPOWDER", "ROTTEN_FLESH", "STRING", "WHEAT", "BREAD", "NAME_TAG", "SADDLE", "COAL", "REDSTONE", "MUSIC_DISC_13", "MUSIC_DISC_CAT",
            "IRON_HORSE_ARMOR", "GOLDEN_APPLE", "BEETROOT_SEEDS", "MELON_SEEDS", "PUMPKIN_SEEDS", "IRON_INGOT", "BUCKET", "ENCHANTED_BOOK", "GOLDEN_HORSE_ARMOR",
            "GOLD_INGOT", "DIAMOND_HORSE_ARMOR", "MUSIC_DISC_OTHERSIDE", "ENCHANTED_GOLDEN_APPLE"),

    VILLAGE_ARMORER = new StructureLoot(new String[]{"chests/village/village_armorer"},
            "BREAD", "IRON_INGOT", "EMERALD", "IRON_HELMET"),
    VILLAGE_BUTCHER = new StructureLoot(new String[]{"chests/village/village_butcher"},
            "BEEF", "MUTTON", "PORKCHOP", "WHEAT", "COAL", "EMERALD"),
    VILLAGE_CARTOGRAPHER = new StructureLoot(new String[]{"chests/village/village_cartographer"},
            "PAPER", "BREAD", "MAP", "STICK", "COMPASS"),
    VILLAGE_FISHER  = new StructureLoot(new String[]{"chests/village/village_fisher"},
            "WHEAT_SEEDS", "COAL", "COD", "BARREL", "SALMON", "WATER_BUCKET", "EMERALD"),
    VILLAGE_FLETCHER  = new StructureLoot(new String[]{"chests/village/village_fletcher"},
            "FEATHER", "FLINT", "STICK", "ARROW", "EGG", "EMERALD"),
    VILLAGE_MASON = new StructureLoot(new String[]{"chests/village/village_mason"},
            "BREAD", "STONE", "STONE_BRICKS", "CLAY_BALL", "EMERALD", "FLOWER_POT", "SMOOTH_STONE", "YELLOW_DYE"),
    VILLAGE_STEPHERD = new StructureLoot(new String[]{"chests/village/village_shepherd"},
            "WHITE_WOOL", "WHEAT", "BLACK_WOOL", "BROWN_WOOL", "GRAY_WOOL", "LIGHT_GRAY_WOOL", "EMERALD", "SHEARS"),
    VILLAGE_TANNERY = new StructureLoot(new String[]{"chests/village/village_tannery"},
            "BREAD", "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS", "EMERALD", "LEATHER", "SADDLE"),
    VILLAGE_TEMPLE = new StructureLoot(new String[]{"chests/village/village_temple"},
            "BREAD", "ROTTEN_FLESH", "REDSTONE", "EMERALD", "LAPIS_LAZULI", "GOLD_INGOT"),
    VILLAGE_TOOLSMITH = new StructureLoot(new String[]{"chests/village/village_toolsmith"},
            "STICK", "BREAD", "IRON_INGOT", "IRON_PICKAXE", "IRON_SHOVEL", "COAL", "GOLD_INGOT", "DIAMOND"),
    VILLAGE_WEAPONSMITH = new StructureLoot(new String[]{"chests/village/village_weaponsmith"},
            "APPLE", "BREAD", "IRON_INGOT", "OAK_SAPLING", "OBSIDIAN", "GOLD_INGOT", "IRON_PICKAXE", "IRON_HELMET", "IRON_CHESTPLATE", "IRON_LEGGINGS",
            "IRON_BOOTS", "DIAMOND", "SADDLE", "IRON_HORSE_ARMOR", "GOLDEN_HORSE_ARMOR", "DIAMOND_HORSE_ARMOR"),
    VILLAGE_DESERT_HOUSE = new StructureLoot(new String[]{"chests/village/village_desert_house"},
            "WHEAT", "BREAD", "CACTUS", "DEAD_BUSH", "EMERALD", "BOOK", "CLAY_BALL", "GREEN_DYE"),
    VILLAGE_PLAIN_HOUSE = new StructureLoot(new String[]{"chests/village/village_plains_house"},
            "POTATO", "APPLE", "BREAD", "OAK_SAPLING", "EMERALD", "DANDELION", "GOLD_NUGGET", "BOOK", "FEATHER", "POPPY"),
    VILLAGE_SAVANNA_HOUSE = new StructureLoot(new String[]{"chests/village/village_savanna_house"},
            "WHEAT_SEEDS", "BREAD", "ACACIA_SAPLING", "GRASS", "SHORT_GRASS", "TALL_GRASS", "EMERALD", "GOLD_NUGGET", "TORCH", "BUCKET", "SADDLE")  ,
    VILLAGE_SNOWY_HOUSE = new StructureLoot(new String[]{"chests/village/village_snowy_house"},
            "POTATO", "SNOWBALL", "BEETROOT_SEEDS", "BREAD", "COAL", "SNOW_BLOCK", "EMERALD", "BEETROOT_SOUP", "BLUE_ICE", "FURNACE"),
    VILLAGE_TAIGA_HOUSE = new StructureLoot(new String[]{"chests/village/village_taiga_house"},
            "POTATO", "SPRUCE_LOG", "BREAD", "SWEET_BERRIES", "PUMPKIN_SEEDS", "SPRUCE_SAPLING", "EMERALD", "FERN", "LARGE_FERN",
            "IRON_NUGGET", "PUMPKIN_PIE", "SPRUCE_SIGN")
    ;

    private final String[] keys;
    private final List<Material> items;

    public StructureLoot(@NotNull String[] keys, @NotNull String... materials) {
        this.keys = keys;
        this.items = new ArrayList<>();

        for (String material : materials) {
            Material mat = Material.getMaterial(material);

            if(mat == null) {
                continue;
            }

            this.items.add(mat);
        }

        for (String key : keys) {
            BY_KEY.put(key, this);
        }
    }

    public String[] getMinecraftKeys() {
        return keys;
    }

    public List<Material> getPossibleLoot() {
        return new ArrayList<>(this.items);
    }

    public static Collection<StructureLoot> values() {
        return BY_KEY.values();
    }

    public static StructureLoot byKey(@NotNull String key) {
        return BY_KEY.get(key);
    }
}
