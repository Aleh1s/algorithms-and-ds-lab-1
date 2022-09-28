package ua.algorithms.lab1.observer;

public interface Observer {

    void handleException(String info);
    void handleException(Exception ex);

}
