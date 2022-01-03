package application.utils.observer;

public interface Observable /*<E extends Event>*/ {
    void addObserver (Observer e);
    void removeObserver(Observer e);
    void notifyObservers();
}