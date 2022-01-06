package application.service;

import application.domain.Message;
import application.domain.User;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.Repository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles the message repository
 */
public class MessageService {

    private final Repository<Integer, Message> repository;
    private final Validator<Message> validator;


    public MessageService(Repository<Integer, Message> repository, Validator<Message> validator) {

        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Returns all messages
     * @return List(Message)
     * @throws SQLException if the database cannot be reached
     */
    public List<Message> getAll() throws RepositoryException, SQLException {
        return repository.getAll();
    }

    /**
     * Finds a message
     * @param id Integer
     * @return Message
     * @throws RepositoryException if the message doesn't exist
     */
    public Message find(Integer id) throws RepositoryException {
        return repository.find(id);
    }

    /**
     * Adds a message to the repository
     * @param from User
     * @param to List(User)
     * @param text String
     * @throws ValidationException if the received params are invalid
     * @throws RepositoryException if the message already exists
     */
    public Message addMessage(User from, List<User> to, String text) throws ValidationException, RepositoryException {

        Message message = new Message(from, to, text, LocalDateTime.now());
        validator.validate(message);

        return repository.add(message);
    }

    /**
     * Adds a reply
     * @param from User
     * @param text String
     * @param replyTo Message
     * @throws ValidationException if the params are invalid
     * @throws RepositoryException if the message being replied to doesn't exist
     */
    public void addReply(User from, String text, Message replyTo) throws ValidationException, RepositoryException {
        Message message = new Message(from, List.of(replyTo.getFrom()), text, LocalDateTime.now());

        message.setReplyOf(replyTo);

        validator.validate(message);

        repository.add(message);
    }

    public void addReplyToAll(User from, String text, Message replyMessage) throws ValidationException, RepositoryException {
        List<User> to = replyMessage.getTo();
        to.remove(from);
        if (!from.equals(replyMessage.getFrom()))
            to.add(replyMessage.getFrom());
        Message message = new Message(from, to, text, LocalDateTime.now());

        message.setReplyOf(replyMessage);
        validator.validate(message);

        repository.add(message);
    }

    /**
     * Returns a message before deleting it
     * @param id Integer
     * @return Message
     * @throws ValidationException if the message is invalid
     * @throws SQLException if the database cannot be reached
     * @throws RepositoryException if the message with that id doesn't exist
     * @throws IOException if the message cannot be parsed
     */
    public Message delete(Integer id) throws RepositoryException {
        return repository.delete(id);
    }

    /**
     * Updates a message and returns the old value
     * @param messageId Integer
     * @param newTo List(User)
     * @param newText String
     * @return Message
     * @throws RepositoryException if the message to update doesn't exist
     * @throws ValidationException if the new message is invalid
     * @throws SQLException if the database cannot be reached
     * @throws IOException if the old message is invalid
     */
    public Message update(Integer messageId, List<User> newTo,  String newText) throws RepositoryException, ValidationException {
        Message message = repository.find(messageId);
        message.setText(newText);
        message.setTo(newTo);

        validator.validate(message);

        return repository.update(message);
    }

    /**
     * Returns the size of the repository
     * @return int
     * @throws RepositoryException if the database cannot be reached
     */
    public int size() throws SQLException {
        return repository.size();
    }

    /**
     * Deletes all the messages from a specific user
     * @param deleted User
     * @throws ValidationException if the messages are invalid
     * @throws SQLException if the database cannot be reached
     * @throws RepositoryException if the user or his messages don't exist
     * @throws IOException if the message contents cannot be parsed
     */
    public void deleteMessagesOfUser(User deleted) throws ValidationException, SQLException, RepositoryException {
        //delete all messages sent by user and user references in to list

        List<Message> messages = getAll();
        for(Message m : messages){
            if (m.getFrom().equals(deleted)){
                delete(m.getId());
            }
            else {

                int initialSize = m.getTo().size();
                boolean found = true;
                while(found){
                    found = false;
                    for (User u : m.getTo()){
                        if (u.equals(deleted)){
                            m.getTo().remove(u);
                            found = true;
                            break;
                        }
                    }
                }
                if (m.getTo().size() == 0){
                    delete(m.getId());
                }
                else if (m.getTo().size() != initialSize){
                    update(m.getId(), m.getTo(), m.getText());
                }
            }
        }
    }


}
