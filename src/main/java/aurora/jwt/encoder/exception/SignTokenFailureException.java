package aurora.jwt.encoder.exception;

public class SignTokenFailureException extends RuntimeException {
    public SignTokenFailureException(Throwable cause) {
        super("Unexpected error occurred when signing token", cause);
    }
}
