package ua.algorithms.lab1.exception;

import java.io.IOException;

public class CloseConnectionException extends IOException {

    public CloseConnectionException(String message) {
        super(message);
    }

    public CloseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseConnectionException(Throwable cause) {
        super(cause);
    }
}
