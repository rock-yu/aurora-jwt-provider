package aurora.jwt.decoder.exception;

public class JwtVerificationException extends RuntimeException {

    public JwtVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtVerificationException(String message) {
        super(message);
    }
}
