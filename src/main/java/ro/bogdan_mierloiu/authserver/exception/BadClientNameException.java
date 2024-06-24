package ro.bogdan_mierloiu.authserver.exception;

public class BadClientNameException extends RuntimeException {
    public BadClientNameException(String message) {
        super(message);
    }
}
