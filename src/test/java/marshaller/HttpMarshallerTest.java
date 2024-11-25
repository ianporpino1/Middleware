package marshaller;

import message.HTTPMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpMarshallerTest {
    private final HttpMarshaller marshaller = new HttpMarshaller();

    @Test
    void testDeserializeValidRequest() throws IOException {
        String httpRequest = """
                GET /resource HTTP/1.1
                Host: localhost
                Content-Type: application/json

                {"key":"value"}
                """;

        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));
        HTTPMessage message = marshaller.deserialize(reader);

        assertEquals("GET", message.httpMethod());
        assertEquals("/resource", message.resource());
        assertEquals("value", message.body().getString("key"));
        assertEquals("localhost", message.headers().get("Host"));
        assertEquals("application/json", message.headers().get("Content-Type"));
    }

    @Test
    void testDeserializeRequestWithoutBody() throws IOException {
        String httpRequest = """
                GET /resource HTTP/1.1
                Host: localhost

                """;

        BufferedReader reader = new BufferedReader(new StringReader(httpRequest));
        HTTPMessage message = marshaller.deserialize(reader);

        assertEquals("GET", message.httpMethod());
        assertEquals("/resource", message.resource());
        assertTrue(message.body().isEmpty());
        assertEquals("localhost", message.headers().get("Host"));
    }

    @Test
    void testSerializeValidResponse() throws IOException {
        HTTPMessage response = new HTTPMessage(
                null,
                null,
                new JSONObject().put("key", "value"),
                200,
                "OK",
                Map.of(
                        "Content-Type", "application/json",
                        "Content-Length", "15"
                )
        );

        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        marshaller.serialize(writer, response);

        String expectedResponse = """
                HTTP/1.1 200 OK
                Content-Type: application/json
                Content-Length: 15

                {"key":"value"}
                """;

        assertEquals(expectedResponse.trim(), stringWriter.toString().trim());
    }

    @Test
    void testSerializeResponseWithoutBody() throws IOException {
        HTTPMessage response = new HTTPMessage(
                null,
                null,
                null,
                204,
                "No Content",
                Map.of("Content-Type", "text/plain")
        );

        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);

        marshaller.serialize(writer, response);

        String expectedResponse = """
                HTTP/1.1 204 No Content
                Content-Type: text/plain

                """;

        assertEquals(expectedResponse.trim(), stringWriter.toString().trim());
    }

    @Test
    void testDeserializeMalformedRequest() {
        String malformedRequest = "INVALID REQUEST";

        BufferedReader reader = new BufferedReader(new StringReader(malformedRequest));
        assertThrows(IllegalArgumentException.class, () -> marshaller.deserialize(reader));
    }

}