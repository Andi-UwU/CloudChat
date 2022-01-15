package application.service;

import application.domain.Message;
import application.domain.User;
import application.domain.validator.Validator;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.database.MessageDataBaseRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Handles the message repository
 */
public class MessageService {

    private final MessageDataBaseRepository repository;
    private final Validator<Message> validator;


    public MessageService(MessageDataBaseRepository repository, Validator<Message> validator) {

        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Returns all messages
     * @return List(Message)
     * @throws RepositoryException if there are no messages in the database
     */
    public List<Message> getAll() throws RepositoryException {
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
     * @throws RepositoryException if the message being replied to, doesn't exist
     */
    public void addReply(User from, String text, Message replyTo) throws ValidationException, RepositoryException {
        Message message = new Message(from, List.of(replyTo.getFrom()), text, LocalDateTime.now());

        message.setReplyOf(replyTo);

        validator.validate(message);

        repository.add(message);
    }

    /**
     * Adds a reply to all
     * @param from User
     * @param text String
     * @param replyMessage Message
     * @throws ValidationException if the message is invalid
     * @throws RepositoryException if the message doesn't exist
     */
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
     * @throws RepositoryException if the message with that id doesn't exist
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
    public int size() throws RepositoryException {
        return repository.size();
    }

    /**
     * Deletes all the messages from a specific user
     * @param deleted User
     * @throws ValidationException if the messages are invalid
     * @throws RepositoryException if the user or his messages don't exist
     */
    public void deleteMessagesOfUser(User deleted) throws ValidationException, RepositoryException {
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

    /**
     * Gets a conversation between 2 users
     * @param user1 User
     * @param user2 User
     * @return List(Message)
     * @throws RepositoryException if the users don't exist
     */
    public List<Message> getConversation(User user1, User user2) throws RepositoryException {
        return repository.getConversation(user1, user2);
    }

    /**
     * Gets the page size of the repository
     * @return Integer
     */
    public Integer getPageSize(){
        return repository.getPageSize();
    }

    /**
     * Gets the number of pages the conversation has
     * @param user1 User
     * @param user2 User
     * @return Integer
     * @throws RepositoryException if the users don't exist
     */
    public Integer getNumberOfConversationPages(User user1, User user2) throws RepositoryException {
        return repository.getNumberOfPages(user1, user2);
    }

    /**
     * Gets a specific page from a conversation
     * @param user1 User
     * @param user2 User
     * @param page Integer
     * @return List(Message)
     * @throws RepositoryException if the users don't exist
     */
    public List<Message> getConversationPage(User user1, User user2, Integer page) throws RepositoryException {
        int maxPage = getNumberOfConversationPages(user1,user2);
        if (page <= 0 || page > maxPage )
            throw new RepositoryException( "Invalid page: " + page + "!\n");

        List<Message> conversation = repository.getPage(user1, user2, page);

        if ( conversation == null)
            return Collections.emptyList();
        else return conversation;
    }


}
