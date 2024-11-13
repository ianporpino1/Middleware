package handler.tcp;

import java.net.Socket;

class TCP_RequestHandler implements Runnable {
    private Socket socket;

    private Invoker invoker;

    private Marshaller marshaller;

    TCP_RequestHandler(Socket socket, Invoker invoker, Marshaller marshaller) {
        this.socket = socket;
        this.invoker = invoker;
        this.marshaller = marshaller;
    }

    @Override
    public void run() {
        //recebe a request
        
        //faz o unmarshall ou ja chama o invoker?
        
        //faz o marshall da resposta
        
        //envia a resposta
    }
}
