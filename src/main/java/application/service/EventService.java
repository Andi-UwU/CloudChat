package application.service;

import application.domain.Event;
import application.domain.User;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.database.EventDataBaseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Services an event repository
 */
public class EventService {

    private final EventDataBaseRepository repository;
    private final Validator<Event> validator;

    /**
     * Constructor
     * @param repository EventDataBaseRepository
     * @param validator Validator(Event)
     */
    public EventService(EventDataBaseRepository repository, Validator<Event> validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Gets all events
     * @return List(Event)
     * @throws RepositoryException if the repository doesn't have any events
     */
    public List<Event> getAll() throws RepositoryException {
        return repository.getAll();
    }

    /**
     * Finds an event by ID
     * @param eventId Integer
     * @return Event
     * @throws RepositoryException if the event doesn't exist
     */
    public Event find(Integer eventId) throws RepositoryException {
        return repository.find(eventId);
    }

    /**
     * Deletes an event
     * @param eventId Integer
     * @return Event
     * @throws RepositoryException if the event doesn't exist
     */
    public Event delete(Integer eventId) throws RepositoryException {
        return repository.delete(eventId);
    }

    /**
     * Adds an event
     * @param author User
     * @param title String
     * @param description String
     * @param eventDate LocalDate
     * @return Event
     * @throws ValidationException if the event is invalid
     * @throws RepositoryException if the event already exists
     */
    public Event add(User author, String title, String description, LocalDate eventDate) throws RepositoryException, ValidationException {
        Event event = new Event(author, title, description, LocalDateTime.now(), eventDate);
        validator.validate(event);

        return repository.add(event);
    }

    /**
     * Updates an event
     * @param event Event
     * @param newTitle String
     * @param newDescription String
     * @return Event
     * @throws RepositoryException if the event to update can't be found
     * @throws ValidationException if the new event is invalid
     */
    public Event update(Event event, String newTitle, String newDescription) throws RepositoryException, ValidationException {
        event.setTitle(newTitle);
        event.setDescription(newDescription);
        validator.validate(event);
        return repository.update(event);
    }

    /**
     * Adds a subscriber to the event
     * @param event Event
     * @param subscriber User
     * @return Event
     * @throws RepositoryException if the user is already subscribed to the event
     */
    public Event addSubscriber(Event event, User subscriber) throws RepositoryException {
        return repository.addSubscriber(event, subscriber);
    }

    /**
     * Removes a subscriber from an event
     * @param event Event
     * @param subscriber User
     * @return Event
     * @throws RepositoryException if the user isn't subscribed to the event
     */
    public Event removeSubscriber(Event event, User subscriber) throws RepositoryException{
        return repository.removeSubscriber(event, subscriber);
    }

    /**
     * Returns the number of events
     * @return Integer
     * @throws RepositoryException if the database can't be reached
     */
    public Integer size() throws RepositoryException {
        return repository.size();
    }
}
