package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import java.io.IOException;

public class JsonUtil {

    /**
     * Converts a JSONObject to a Java object of the specified class type.
     *
     * @param jsonObject the input JSONObject
     * @param clazz the class type of the target object
     * @param <T> the type of the target object
     * @return the deserialized Java object
     */
    public static <T> T fromJson(JSONObject jsonObject, Class<T> clazz) {
        if (jsonObject == null) return  null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonObject.toString(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
