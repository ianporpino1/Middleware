package marshaller;

import message.HTTPMessage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpMarshaller implements IHttpMarshaller{

    @Override
    public HTTPMessage deserialize(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("Invalid HTTP request");
        }

        String[] startLineParts = startLine.split(" ");
        if (startLineParts.length < 3) {
            throw new IllegalArgumentException("Malformed HTTP start line");
        }

        String method = startLineParts[0];
        String resource = startLineParts[1];
        String version = startLineParts[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int separatorIndex = line.indexOf(":");
            if (separatorIndex > 0) {
                String headerName = line.substring(0, separatorIndex).trim();
                String headerValue = line.substring(separatorIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        StringBuilder bodyBuilder = new StringBuilder();
        while (reader.ready() && (line = reader.readLine()) != null) {
            bodyBuilder.append(line).append("\n");
        }

        JSONObject body = bodyBuilder.length() > 0 ? new JSONObject(bodyBuilder.toString().trim()) : new JSONObject();

        return new HTTPMessage(method, resource, body, 0, null, headers);
    }

    @Override
    public void serialize(BufferedWriter writer, HTTPMessage response) throws IOException {
        writer.write("HTTP/1.1 " + response.statusCode() + " " + response.statusMessage() + "\r\n");
        for (var entry : response.headers().entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        writer.write("\r\n");
        if (response.body() != null) {
            writer.write(response.body().toString());
        }
        writer.flush();
    }
}

