package dev.spoocy.utils.config.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonProcessor {

     public JsonProcessor() {

     }

     public static Map<String, Object> toJsonMap(@NotNull String json) {
         return toMap(new JSONObject(json));
     }

     private static Map<String, Object> toMap(@NotNull JSONObject json) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : json.keySet()) {
            Object val = json.get(key);
            map.put(key, toObject(val));
        }
        return map;
    }

    private static Object toObject(@Nullable Object val) {
        if (val instanceof JSONObject) {
            return toMap((JSONObject) val);
        }

        if (val instanceof JSONArray) {
            JSONArray arr = (JSONArray) val;
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                Object element = toObject(arr.get(i));
                list.add(element);
            }
            return list;
        }

        if (val == JSONObject.NULL) {
            return null;
        }
        return val;
    }
}
