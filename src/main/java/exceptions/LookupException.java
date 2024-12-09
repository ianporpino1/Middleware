package exceptions;

public class LookupException extends RemoteErrorException {
    public LookupException(String route) {
        super("Rota para classe não encontrada: " + route);
    }
}
