package dev.spoocy.utils.bukkit.serializers;

import dev.spoocy.utils.bukkit.biz.source_code.base64Coder.Base64Coder;
import dev.spoocy.utils.common.exceptions.WrappedException;
import dev.spoocy.utils.config.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class InventorySerializer implements Serializer<Inventory> {

    public static final InventorySerializer INSTANCE = new InventorySerializer();

    private InventorySerializer() {
        super();
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Inventory object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(object.getContents());
            dataOutput.close();

            String base64 = Base64Coder.encodeLines(outputStream.toByteArray());

            return Map.of(
                "type", object.getType().name(),
                "size", object.getSize(),
                "base64", base64
            );
        } catch (Throwable e) {
            WrappedException.rethrow(e);
        }

        throw new IllegalStateException("No Handle");
    }

    @Override
    public @NotNull Inventory deserialize(@NotNull Map<String, Object> map) {
        if (!map.containsKey("type")) {
            throw new IllegalArgumentException("Map does not contain key 'type'.");
        }
        if (!map.containsKey("size")) {
            throw new IllegalArgumentException("Map does not contain key 'size'.");
        }
        if (!map.containsKey("base64")) {
            throw new IllegalArgumentException("Map does not contain key 'base64'.");
        }

        try {
            String typeName = (String) map.get("type");
            int size = ((Number) map.get("size")).intValue();
            String base64 = (String) map.get("base64");

            InventoryType type = InventoryType.valueOf(typeName);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] contents = (ItemStack[]) dataInput.readObject();
            dataInput.close();

            Inventory inventory;
            if (type == InventoryType.CHEST) {
                if (size <= 0 || size > 54 || size % 9 != 0) {
                    throw new IllegalArgumentException("Invalid chest inventory size: " + size);
                }

                inventory = Bukkit.createInventory(null, size);
            } else {
                inventory = Bukkit.createInventory(null, type);
            }

            if (contents.length != inventory.getSize()) {
                ItemStack[] resized = new ItemStack[inventory.getSize()];
                System.arraycopy(contents, 0, resized, 0, Math.min(contents.length, resized.length));
                contents = resized;
            }

            inventory.setContents(contents);
            return inventory;
        } catch (Exception e) {
            WrappedException.rethrow(e);
        }

        throw new IllegalStateException("No Handle");
    }
}
