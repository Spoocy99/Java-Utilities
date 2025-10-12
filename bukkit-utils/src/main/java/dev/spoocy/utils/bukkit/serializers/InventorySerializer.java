package dev.spoocy.utils.bukkit.serializers;

import dev.spoocy.utils.common.exceptions.WrappedException;
import dev.spoocy.utils.config.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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

            dataOutput.writeInt(object.getSize());

            for (int i = 0; i < object.getSize(); i++) {
                dataOutput.writeObject(object.getItem(i));
            }

            dataOutput.close();
            String base64 = Base64Coder.encodeLines(outputStream.toByteArray());

            return Map.of("base64", base64);
        } catch (Throwable e) {
            WrappedException.rethrow(e);
        }

        throw new IllegalStateException("No Handle");
    }

    @Override
    public @NotNull Inventory deserialize(@NotNull Map<String, Object> map) {
        if(!map.containsKey("base64")) {
            throw new IllegalArgumentException("Map does not contain key 'base64'.");
        }

        try {
            String base64 = (String) map.get("base64");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (Throwable e) {
            WrappedException.rethrow(e);
        }

        throw new IllegalStateException("No Handle");
    }
}
