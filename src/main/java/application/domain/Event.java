package application.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Event extends Entity<Integer>{

    User author;
    String title;
    String description;
    LocalDateTime creationDate;
    LocalDate eventDate;
    List<User> subscribers;

    /**
     * Constructor
     * @param author User
     * @param title String
     * @param description String
     * @param creationDate LocalDate
     * @param eventDate LocalDate
     */
    public Event(User author, String title, String description, LocalDateTime creationDate, LocalDate eventDate) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.eventDate = eventDate;
    }

    /**
     * Gets event author
     * @return User
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Sets event author
     * @param author User
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Gets event title
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets event title
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets event description
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets event description
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets event creation date
     * @return LocalDateTime
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets event creation date
     * @param creationDate LocalDateTime
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets event date
     * @return LocalDate
     */
    public LocalDate getEventDate() {
        return eventDate;
    }

    /**
     * Sets event date
     * @param eventDate LocalDate
     */
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Gets the list of subscribers
     * @return List(User)
     */
    public List<User> getSubscribers() {
        return subscribers;
    }

    /**
     * Sets the list of subscribers
     * @param subscribers List(User)
     */
    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return title + ':' + description;
    }
}
