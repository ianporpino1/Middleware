package marshaller;

import message.HTTPMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface IHttpMarshaller {
//  pega mensagem do buffer e transforma em obj http
    HTTPMessage deserialize(BufferedReader reader) throws IOException;

    // pega obj http e escreve no buffer do socket
    void serialize(BufferedWriter writer, HTTPMessage response) throws IOException;
}
