package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.exception.CloseConnectionException;
import ua.algorithms.lab1.exception.FileAccessException;
import ua.algorithms.lab1.observer.Observable;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;

public interface Model extends Closeable {

    void sort() throws FileNotFoundException, CloseConnectionException, FileAccessException;

}
