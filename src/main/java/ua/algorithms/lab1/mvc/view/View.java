package ua.algorithms.lab1.mvc.view;

import ua.algorithms.lab1.observer.Observer;

import java.io.File;

public interface View extends Observer {

    boolean openSourceFile(String path);
    boolean sortSourceFile(File source);

}
