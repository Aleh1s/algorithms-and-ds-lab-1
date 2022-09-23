package ua.algorithms.lab1.observer;

public interface Observer {

    void update(String info);
    void update(Exception ex);

}
