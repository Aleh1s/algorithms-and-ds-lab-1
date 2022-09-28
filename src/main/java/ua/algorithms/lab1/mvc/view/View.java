package ua.algorithms.lab1.mvc.view;

import ua.algorithms.lab1.exception.FetchFileLengthException;
import ua.algorithms.lab1.observer.Observer;

import java.io.File;
import java.io.FileNotFoundException;

public interface View {

    String getSourceFilePath();
    boolean openSourceFile(String path);
    boolean sortSourceFile(String source, String choice);

    void handleException(Exception e);
}
