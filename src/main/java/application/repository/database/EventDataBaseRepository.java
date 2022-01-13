package application.repository.database;

import application.domain.Event;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.utils.Pagination;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDataBaseRepository extends DataBaseRepository<Integer, Event>{
    /**
     * Constructor
     *
     * @param url      String
     * @param username String
     * @param password String
     */
    public EventDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public Event find(Integer eventId) throws RepositoryException {
        String getEventSql =
                "select event.id as event_id, author_id, title, description, creation_date, event_date,\n" +
                "       users.id as user_id, first_name, last_name, username\n" +
                "from event\n" +
                "inner join users on event.author_id = users.id\n" +
                "where event.id = ?";
        String getSubscribersSql = "select * from users\n" +
                "where users.id in \n" +
                "(select user_id from subscribed where event_id = ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement eventStatement = connection.prepareStatement(getEventSql);
             PreparedStatement subscribersStatement = connection.prepareStatement(getSubscribersSql)) {

            //get fields from message table
            eventStatement.setInt(1, eventId);
            ResultSet eventResultSet = eventStatement.executeQuery();
            eventResultSet.next();

            //author user
            Integer userId = eventResultSet.getInt("user_id");
            String firstName = eventResultSet.getString("first_name");
            String lastName = eventResultSet.getString("last_name");
            String userName = eventResultSet.getString("username");
            User author = new User(firstName, lastName, userName);
            author.setId(userId);

            //event
            String title = eventResultSet.getString("title");
            String description = eventResultSet.getString("description");
            LocalDateTime creationDate = LocalDateTime.parse(eventResultSet.getString("creation_date"));
            LocalDate eventDate = LocalDate.parse(eventResultSet.getString("event_date"));

            Event event = new Event(author, title, description, creationDate, eventDate);
            event.setId(eventId);
            eventResultSet.close();

            //subscribers list
            subscribersStatement.setInt(1, eventId);
            ResultSet subscribersResultSet = subscribersStatement.executeQuery();

            List<User> subscribersList = new ArrayList<>();
            while(subscribersResultSet.next()){
                Integer subscriberUserId = subscribersResultSet.getInt("id");
                String subscriberFirstName = subscribersResultSet.getString("first_name");
                String subscriberLastName = subscribersResultSet.getString("last_name");
                String subscriberUserName = subscribersResultSet.getString("username");
                User subscriber = new User(subscriberFirstName, subscriberLastName, subscriberUserName);
                subscriber.setId(subscriberUserId);
                subscribersList.add(subscriber);
            }
            event.setSubscribers(subscribersList);

            return event;
        } catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent message!\n");
        }
    }


    @Override
    public List<Event> getAll() throws RepositoryException {

        String getEventSql =
                "select event.id as event_id, author_id, title, description, creation_date, event_date,\n" +
                "       users.id as user_id, first_name, last_name, username\n" +
                "from event\n" +
                "inner join users on event.author_id = users.id;";
        String getSubscribersSql = "select * from users\n" +
                "where users.id in \n" +
                "(select user_id from subscribed where event_id = ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement eventStatement = connection.prepareStatement(getEventSql);
             PreparedStatement subscribersStatement = connection.prepareStatement(getSubscribersSql)) {


            ResultSet eventResultSet = eventStatement.executeQuery();
            List<Event> eventList = new ArrayList<>();
            while(eventResultSet.next()){
                //author user
                Integer userId = eventResultSet.getInt("user_id");
                String firstName = eventResultSet.getString("first_name");
                String lastName = eventResultSet.getString("last_name");
                String userName = eventResultSet.getString("username");
                User author = new User(firstName, lastName, userName);
                author.setId(userId);

                //event
                Integer eventId = eventResultSet.getInt("event_id");
                String title = eventResultSet.getString("title");
                String description = eventResultSet.getString("description");
                LocalDateTime creationDate = LocalDateTime.parse(eventResultSet.getString("creation_date"));
                LocalDate eventDate = LocalDate.parse(eventResultSet.getString("event_date"));

                Event event = new Event(author, title, description, creationDate, eventDate);
                event.setId(eventId);

                //subscribers list
                subscribersStatement.setInt(1, eventId);
                ResultSet subscribersResultSet = subscribersStatement.executeQuery();

                List<User> subscribersList = new ArrayList<>();
                while(subscribersResultSet.next()){
                    Integer subscriberUserId = subscribersResultSet.getInt("id");
                    String subscriberFirstName = subscribersResultSet.getString("first_name");
                    String subscriberLastName = subscribersResultSet.getString("last_name");
                    String subscriberUserName = subscribersResultSet.getString("username");
                    User subscriber = new User(subscriberFirstName, subscriberLastName, subscriberUserName);
                    subscriber.setId(subscriberUserId);
                    subscribersList.add(subscriber);
                }
                event.setSubscribers(subscribersList);
                eventList.add(event);
            }

            return eventList;

        } catch (SQLException throwable) {
            throw new RepositoryException(throwable.getMessage());
        }
    }

    @Override
    public Event add(Event event) throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement eventStatement = connection.prepareStatement(
                     "insert into event (id, author_id, title, description, creation_date, event_date) values (?, ?, ?, ?, ?, ?)");
             PreparedStatement nextIdStatement = connection.prepareStatement(
                     "SELECT nextval('event_id_seq');")
        ) {
            //get the next id
            ResultSet idResultSet = nextIdStatement.executeQuery();
            idResultSet.next();
            int eventId = idResultSet.getInt("nextval");
            //insert into event table
            eventStatement.setInt(1, eventId);
            eventStatement.setInt(2, event.getAuthor().getId());
            eventStatement.setString(3, event.getTitle());
            eventStatement.setString(4, event.getDescription());
            eventStatement.setString(5, event.getCreationDate().toString());
            eventStatement.setString(6, event.getEventDate().toString());

            eventStatement.executeUpdate();

            event.setId(eventId);
            return event;
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    public Event addSubscriber(Event event, User user) throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement eventStatement = connection.prepareStatement(
                     "insert into subscribed (user_id, event_id) values (?, ?)");

        ) {
            //insert into event table
            eventStatement.setInt(1, user.getId());
            eventStatement.setInt(2, event.getId());

            eventStatement.executeUpdate();

            List<User>subscribers = event.getSubscribers();
            subscribers.add(user);
            event.setSubscribers(subscribers);
            return event;
        } catch (SQLException e) {
            throw new RepositoryException("You already are subscribed to this event!\n");
        }
    }
    public Event removeSubscriber(Event event, User user) throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "delete from subscribed where user_id = ? and event_id = ?");
        ) {
            List<User>subscribers = event.getSubscribers();
            if (!subscribers.contains(user)){
                throw new RepositoryException("You are not subscribed to this event!\n");
            }
            statement.setInt(1, user.getId());
            statement.setInt(2, event.getId());
            statement.executeUpdate();



            subscribers.remove(user);
            event.setSubscribers(subscribers);
            return event;

        } catch (SQLException throwable) {
            throw new RepositoryException(throwable.getMessage());
        }
    }

    @Override
    public Event delete(Integer eventId) throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "delete from event where id = ?");
        ) {

            Event event = find(eventId);
            statement.setInt(1, eventId);
            statement.executeUpdate();

            return event;

        } catch (SQLException throwable) {
            throw new RepositoryException(throwable.getMessage());
        }
    }

    @Override
    public Event update(Event event) throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "update message set title = ?, description = ? where id = ?");

        ) {
            //update text
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setInt(3, event.getId());
            statement.executeUpdate();

            return event;

        } catch (SQLException throwable) {
            throw new RepositoryException(throwable.getMessage());
        }
    }

    @Override
    public Integer size() throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as count from event");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt("count");
        } catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public List<Event> getPage(Integer page) throws RepositoryException, IllegalArgumentException {
        return Pagination.<Event>getPage(getAll(), page, pageSize);
    }

    @Override
    public int getNumberOfPages() throws RepositoryException {
        int size = size();
        int mod = size % pageSize;
        int additionalPage = 0;
        if (mod > 0) additionalPage = 1;
        return (size / pageSize + additionalPage);
    }
}
