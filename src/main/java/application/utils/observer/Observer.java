package application.utils.observer;

/**
 * Interface for an observer entity
 */
public interface Observer /*<E extends Event>*/ {
    
    /**
     * Updates the observer entity once the observed object sends a notification
     */
    void observerUpdate();
}
