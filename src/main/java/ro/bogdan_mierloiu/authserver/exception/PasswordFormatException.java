package ro.bogdan_mierloiu.authserver.exception;

public class PasswordFormatException extends RuntimeException {

    public PasswordFormatException(String message) {
        super(message);
    }
}
