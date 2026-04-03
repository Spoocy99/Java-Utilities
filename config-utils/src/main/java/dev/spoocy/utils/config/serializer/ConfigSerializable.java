package dev.spoocy.utils.config.serializer;

import java.util.Map;

/**
 * Interface for serializable configuration objects.
 * <p>
 * When implementing this interface, the class is expected to
 * contain a public static method {@code deserialize(Map<String, Object> map)} that returns a
 * new instance of the class from a Map<String, Object>.
 * <pre>
 * {@code
 * public static MyClass deserialize(Map<String, Object> map) {
 *      // Deserialize the map into an instance of MyClass
 *      return new MyClass(...);
 * }
 *
 * }
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface ConfigSerializable {

    Map<String, Object> serialize();

}
