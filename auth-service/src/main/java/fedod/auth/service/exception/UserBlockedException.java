package fedod.auth.service.exception;

public class UserBlockedException extends RuntimeException {

    public UserBlockedException(String message) {
        super(message);
    }
}
