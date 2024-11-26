package marshaller;

import message.HTTPMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface Marshaller {
    HTTPMessage deserialize(BufferedReader reader) throws IOException;

    void serialize(BufferedWriter writer, HTTPMessage response) throws IOException;
}
