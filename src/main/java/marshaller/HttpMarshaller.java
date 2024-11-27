package marshaller;

import message.HTTPMessage;
import message.HttpRequest;
import message.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpMarshaller implements Marshaller {

    @Override
    public HttpRequest deserialize(String httpString) throws IOException {
        System.out.println(httpString);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHeaders(new HashMap<>());

        String[] lines = httpString.split("\r\n");
        
        String[] requestLine = lines[0].split(" ");
        if (requestLine.length >= 2) {
            httpRequest.setMethod(requestLine[0]);
            httpRequest.setUrl(requestLine[1]);
        }

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] headerParts = lines[i].split(":", 2);
            if (headerParts.length == 2) {
                String key = headerParts[0].trim();
                String value = headerParts[1].trim();
                httpRequest.getHeaders().put(key, value);
            }
            i++;
        }
      
        StringBuilder bodyBuilder = new StringBuilder();
        for (int j = i + 1; j < lines.length; j++) {
            bodyBuilder.append(lines[j]).append("\r\n");
        }
        httpRequest.setBody(bodyBuilder.toString().trim());

        return httpRequest;
    }

    @Override
    public String serialize(HttpResponse response) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append("HTTP/1.1 ")
                .append(response.getStatusCode())
                .append(" ")
                .append(response.getStatusMessage())
                .append("\r\n");

        if(response.getHeaders() != null) {
            for (var entry : response.getHeaders().entrySet()) {
                responseBuilder.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\r\n");
            }
        }
        
        responseBuilder.append("\r\n");

        if (response.getBody() != null) {
            responseBuilder.append(response.getBody());
        }
        return responseBuilder.toString();
    }
}

