package message;

import org.json.JSONObject;

import java.io.Serializable;

public record HTTPMessage(
        String httpMethod,
        String resource,
        JSONObject body
) implements Serializable {}