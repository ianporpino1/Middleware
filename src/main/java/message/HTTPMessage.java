package message;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

public record HTTPMessage(
        String httpMethod,
        String resource,
        JSONObject body,
        int statusCode,
        String statusMessage,
        Map<String, String> headers
) implements Serializable {}
