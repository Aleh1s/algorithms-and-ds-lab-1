package ua.algorithms.lab1.exception;

public class CanNotCreateDestinationFileException extends RuntimeException {
    public CanNotCreateDestinationFileException() {
    }

    public CanNotCreateDestinationFileException(String message) {
        super(message);
    }

    public CanNotCreateDestinationFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotCreateDestinationFileException(Throwable cause) {
        super(cause);
    }
}
