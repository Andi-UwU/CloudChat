package application.service;

import application.domain.Event;
import application.domain.User;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.database.EventDataBaseRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EventService {

    private final EventDataBaseRepository repository;
    private final Validator<Event> validator;

    public EventService(EventDataBaseRepository repository, Validator<Event> validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Event> getAll() throws RepositoryException {
        return repository.getAll();
    }

    public Event find(Integer eventId) throws RepositoryException {
        return repository.find(eventId);
    }

    public Event delete(Integer eventId) throws RepositoryException {
        return repository.delete(eventId);
    }

    public Event add(User author, String title, String description, LocalDate eventDate) throws ValidationException, RepositoryException {
        Event event = new Event(author, title, description, LocalDateTime.now(), eventDate);
        validator.validate(event);

        return repository.add(event);
    }

    public Event update(Event event, String newTitle, String newDescription) throws RepositoryException, ValidationException {
        event.setTitle(newTitle);
        event.setDescription(newDescription);
        validator.validate(event);
        return repository.update(event);
    }

    public Event addSubscriber(Event event, User subscriber) throws RepositoryException {
        return repository.addSubscriber(event, subscriber);
    }
    public Event removeSubscriber(Event event, User subscriber) throws RepositoryException{
        return repository.removeSubscriber(event, subscriber);
    }

    public Integer size() throws RepositoryException {
        return repository.size();
    }
}
