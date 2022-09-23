package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.observer.Observable;

import java.io.File;

public interface Model extends Observable {

    void sort(File source);

}
