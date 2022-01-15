package application.utils.observer;

/**
 * Interface for an observable entity
 */
public interface Observable /*<E extends Event>*/ {
    /**
     * Adds an observer to the object
     * @param e Observer
     */
    void addObserver (Observer e);

    /**
     * Removes an observer from the object
     * @param e Observer
     */
    void removeObserver(Observer e);

    /**
     * Notifies all observers
     */
    void notifyObservers();
}