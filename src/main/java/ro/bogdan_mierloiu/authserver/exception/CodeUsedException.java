package ro.bogdan_mierloiu.authserver.exception;

public class CodeUsedException extends RuntimeException {
    public CodeUsedException(String message) {
        super(message);
    }
}
