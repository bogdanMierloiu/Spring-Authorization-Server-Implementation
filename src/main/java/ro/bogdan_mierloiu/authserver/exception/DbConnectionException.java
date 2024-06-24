package ro.bogdan_mierloiu.authserver.exception;

public class DbConnectionException extends RuntimeException {
    public DbConnectionException(String message) {
        super(message);
    }
}
