package handler.http;

import handler.interfaces.IHandler;
import invoker.Invoker;
import message.HttpRequest;
import message.HttpResponse;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HTTP_RequestHandler implements Runnable, IHandler {
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
        handle(clientSocket);
    }

    @Override
    public void handle(Socket clientSocket) {
        //recebe a request
        HttpRequest request = readRequest();
        if(request == null) {
            sendResponse(null);
            return;
        }
        //interceptors

        //faz o unmarshall ou ja chama o invoker?
        HttpResponse response;
        try {
            System.out.println(request);
            response = invoker.invoke(request);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //interceptors

        //faz o marshall da resposta
        sendResponse(response);
    }

    private void sendResponse(HttpResponse response) {
        try {
            if(response == null) {
                response = new HttpResponse();
                response.setStatusCode(404);
                response.setStatusMessage("Not Found");
                response.setBody("erro");
            }
            String httpResponse = "HTTP/1.1 " + response.getStatusCode() +" " + response.getStatusMessage() + "\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + response.getBody().getBytes().length + "\r\n" +
                    "\r\n" +
                    response.getBody();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            writer.write(httpResponse);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao enviar a resposta HTTP", e);
        }
    }

    private HttpRequest readRequest() {
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
            
            
            var request = new HttpRequest();
            request.setMethod(method);
            request.setUrl(route);

            String headerLine;
            int contentLength = 0;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                String[] headerParts = headerLine.split(":");
                request.addHeader(headerParts[0], headerParts[1]);
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

            return request;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler a requisição HTTP", e);
        }
    }

    
}
