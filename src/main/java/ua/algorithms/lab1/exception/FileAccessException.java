package ua.algorithms.lab1.exception;

import java.io.IOException;

public class FileAccessException extends IOException {
    public FileAccessException() {
    }

    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAccessException(Throwable cause) {
        super(cause);
    }
}
