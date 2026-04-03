package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.serializer.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SerializableExample implements ConfigSerializable {

    public static final SerializableExample EXAMPLE = new SerializableExample("example", 42);

    private final String name;
    private final int value;

    public SerializableExample(@NotNull String name, int value) {
        this.name = name;
        this.value = value;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "name", this.name,
                "value", this.value
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SerializableExample that = (SerializableExample) obj;
        return value == that.value && name.equals(that.name);
    }

    public static SerializableExample deserialize(@NotNull Map<String, Object> map) {
        String name = (String) map.get("name");
        int value = (int) map.get("value");
        return new SerializableExample(name, value);
    }

}
