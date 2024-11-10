public class Middleware {
    
    public void start(int port, String protocol) {
        
        System.out.println("Middleware started");
    }
    
    
    public static void main(String[] args) {
        System.out.println("Test " + Middleware.class);
    }
}
