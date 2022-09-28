package ua.algorithms.lab1.exception;

public class FileCreateException extends RuntimeException {
    public FileCreateException() {
    }

    public FileCreateException(String message) {
        super(message);
    }

    public FileCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileCreateException(Throwable cause) {
        super(cause);
    }
}
