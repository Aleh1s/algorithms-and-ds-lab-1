package ua.algorithms.lab1.exception;

public class FetchFileLengthException extends FileAccessException {
    public FetchFileLengthException() {
    }

    public FetchFileLengthException(String message) {
        super(message);
    }

    public FetchFileLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetchFileLengthException(Throwable cause) {
        super(cause);
    }
}
