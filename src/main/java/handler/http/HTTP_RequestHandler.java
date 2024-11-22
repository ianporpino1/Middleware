package handler.http;

import invoker.Invoker;
import message.HTTPMessage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTP_RequestHandler implements Runnable {
    private Socket clientSocket;

    private Invoker invoker;

    //private Marshaller marshaller;

    public HTTP_RequestHandler(Socket clientSocket, Invoker invoker) {
        this.clientSocket = clientSocket;
        this.invoker = invoker;
        //talvez o broker que tenha que criar o marshaller, pois dessa forma cada thread tera um
        //this.marshaller = new Marshaller();
    }

    @Override
    public void run() {
        //recebe a request
        HTTPMessage httpMessage = readRequest();

        //faz o unmarshall ou ja chama o invoker?
        //HTTPMessage response = invoker.invoke(httpMessage);

        //faz o marshall da resposta
        sendResponse(null);
    }

    private void sendResponse(HTTPMessage response) {
        try {
            String body = "response.body().toString()";
            String statusLine = "HTTP/1.1 200 OK";
            String httpResponse = statusLine + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "\r\n" +
                    body;

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            writer.write(httpResponse);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar a resposta HTTP", e);
        }
    }

    private HTTPMessage readRequest() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String inputLine = reader.readLine();
            if (inputLine == null || inputLine.isEmpty()) {
                return null;
            }

            String[] requestLineParts = inputLine.split(" ");
            String method = requestLineParts[0];
            String route = requestLineParts[1];
            String protocol = requestLineParts[2];// HTTP/1.1

            String headerLine;
            int contentLength = 0;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                }
            }

            StringBuilder bodyBuilder = new StringBuilder();
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                bodyBuilder.append(body);
            }
            System.out.println(method + " " + route + " " + headerLine + contentLength);
            
            //marshaller para deserializar body.

            return new HTTPMessage(method, route, new JSONObject(bodyBuilder));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler a requisição HTTP", e);
        }
    }
}
