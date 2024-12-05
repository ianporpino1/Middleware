package application;


import annotation.MiddlewareApplication;

@MiddlewareApplication
public class Main {

    public static void main(String[] args) {
        //acho q protocol plugin poderia ser implementado da seguinte maneira: 
        //Interface handler em que o usuario do middleware q quer um protocolo dif implementaria.

        //como permitir q o cliente do middleware adicione comportamentos do extension aqui?

        broker.MiddlewareApplication.run(Main.class, args);
    }
}
