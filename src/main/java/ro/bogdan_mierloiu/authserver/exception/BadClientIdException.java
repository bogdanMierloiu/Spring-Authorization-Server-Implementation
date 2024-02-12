package ro.bogdan_mierloiu.authserver.exception;

public class BadClientIdException extends RuntimeException {
    public BadClientIdException(String message) {
        super(message);
    }
}
