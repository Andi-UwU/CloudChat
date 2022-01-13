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

    public Event(User author, String title, String description, LocalDateTime creationDate, LocalDate eventDate) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.eventDate = eventDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return title + ':' + description;
    }
}
