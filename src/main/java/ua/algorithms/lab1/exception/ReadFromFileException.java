package ua.algorithms.lab1.exception;

import java.io.IOException;

public class ReadFromFileException extends FileAccessException {

    public ReadFromFileException() {
    }

    public ReadFromFileException(String message) {
        super(message);
    }

    public ReadFromFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadFromFileException(Throwable cause) {
        super(cause);
    }
}
