package ua.algorithms.lab1.exception;

public class WriteToFileException extends FileAccessException {
    public WriteToFileException() {
    }

    public WriteToFileException(String message) {
        super(message);
    }

    public WriteToFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteToFileException(Throwable cause) {
        super(cause);
    }
}
