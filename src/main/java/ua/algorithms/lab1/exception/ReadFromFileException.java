package ua.algorithms.lab1.exception;

import java.io.IOException;

public class ReadFromFileException extends FileAccessException {
    public ReadFromFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
