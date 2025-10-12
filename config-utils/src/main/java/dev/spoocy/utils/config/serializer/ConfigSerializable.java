package dev.spoocy.utils.config.serializer;

import java.util.Map;
/**
 * Interface for serializable configuration objects.
 * Instead of using {@link ConfigSerializer}.
 * <p>
 * When implementing this interface, the class must
 * contain a public static method `deserialize(Map<String, Object> map)` that returns a
 * new instance of the class from a Map<String, Object>.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface ConfigSerializable {

    Map<String, Object> serialize();

}
