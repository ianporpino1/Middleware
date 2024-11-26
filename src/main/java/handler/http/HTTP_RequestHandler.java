package handler.http;

import handler.interfaces.IHandler;
import invoker.Invoker;
import message.HTTPMessage;
import message.HttpRequest;
import message.HttpResponse;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTP_RequestHandler implements Runnable, IHandler {
    private final Socket clientSocket;

    private final Invoker invoker;

    public HTTP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        handle(clientSocket);
    }

    @Override
    public void handle(Socket clientSocket) {
        HTTPMessage httpMessage;

        try {
            httpMessage = invoker.invoke(clientSocket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //interceptors

        sendResponse(httpMessage);

        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(HTTPMessage response) {
        try {
            if(response == null) {
//                response = new HTTPMessage(
//
//                        response.statusCode(500);
//                response.statusMessage("Internal Error.");
//                response.body("erro");
//                );
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));

            writer.write(response.body().toString());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar a resposta HTTP", e);
        }
    }

//    private HttpRequest readRequest() {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
//            String inputLine = reader.readLine();
//            if (inputLine == null || inputLine.isEmpty()) {
//                return null;
//            }
//
//            String[] requestLineParts = inputLine.split(" ");
//            String method = requestLineParts[0];
//            String route = requestLineParts[1];
//            String protocol = requestLineParts[2];// HTTP/1.1
//
//
//            var request = new HttpRequest();
//            request.setMethod(method);
//            request.setUrl(route);
//
//            String headerLine;
//            int contentLength = 0;
//            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
//                headerLine = headerLine.trim();
//                String[] headerParts = headerLine.split(":", 2);
//                String key = headerParts[0].trim();
//                String value = headerParts[1].trim();
//
//                request.addHeader(key, value);
//
//                if (key.equalsIgnoreCase("Content-Length")) {
//                    contentLength = Integer.parseInt(value);
//                }
//            }
//
//            StringBuilder bodyBuilder = new StringBuilder();
//            if (contentLength > 0) {
//                char[] body = new char[contentLength];
//                reader.read(body, 0, contentLength);
//                bodyBuilder.append(body);
//            }
//            System.out.println(method + " " + route + " " + headerLine + contentLength);
//            request.setBody(bodyBuilder.toString());
//
//            return request;
//        } catch (IOException e) {
//            throw new RuntimeException("Erro ao ler a requisição HTTP", e);
//        }
//    }
}
