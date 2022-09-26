package ua.algorithms.lab1.mvc.model;

import ua.algorithms.lab1.observer.Observer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileSystem implements Model {


    private List<Observer> observers;

    {
        observers = new LinkedList<>();
    }

    @Override
    public void sort(File source) {

    }

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String info) {
        for (Observer o : observers) o.update(info);
    }

    @Override
    public void notifyObservers(Exception ex) {
        for (Observer o : observers) o.update(ex);
    }

}
