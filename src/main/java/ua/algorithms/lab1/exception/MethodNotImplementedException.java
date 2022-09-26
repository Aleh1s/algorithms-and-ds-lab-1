package ua.algorithms.lab1.exception;

public class MethodNotImplementedException extends RuntimeException {

    public MethodNotImplementedException() {
    }

    public MethodNotImplementedException(String message) {
        super(message);
    }

    public MethodNotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotImplementedException(Throwable cause) {
        super(cause);
    }
}
