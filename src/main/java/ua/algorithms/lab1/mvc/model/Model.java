package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.exception.CloseConnectionException;
import ua.algorithms.lab1.exception.FileAccessException;

import java.io.FileNotFoundException;

public interface Model {

    void sort() throws FileNotFoundException, CloseConnectionException, FileAccessException;

}
