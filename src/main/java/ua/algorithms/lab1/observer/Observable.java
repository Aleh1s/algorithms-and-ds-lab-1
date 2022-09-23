package ua.algorithms.lab1.observer;

public interface Observable {

    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(String info);
    void notifyObservers(Exception ex);

}
