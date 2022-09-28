package ua.algorithms.lab1.exception;

public class WriteToFileException extends FileAccessException {
    public WriteToFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
