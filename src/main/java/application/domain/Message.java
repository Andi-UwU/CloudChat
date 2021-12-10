package application.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Represents a message sent from a user to other users
 */
public class Message extends Entity<Integer> {

    private User from;
    private List<User> to;
    private String text;
    private LocalDateTime date;
    private Optional<Message> replyOf;


    /**
     * Gets the message this message replies to
     * @return Optional(Message)
     */
    public Optional<Message> getReplyOf() {
        return replyOf;
    }

    /**
     * Sets the message this one replies to
     * @param replyOfMessage Message
     */
    public void setReplyOf(Message replyOfMessage) {
        this.replyOf = Optional.ofNullable(replyOfMessage);
    }

    /**
     * Constructor
     * @param from User
     * @param to List(User)
     * @param text String
     * @param date LocalDateTime
     */
    public Message(User from, List<User> to, String text, LocalDateTime date){
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
        this.replyOf = Optional.empty();
    }

    /**
     * Gets the user who sent the message
     * @return User
     */
    public User getFrom() {
        return from;
    }

    /**
     * Gets the list of users the message was sent to
     * @return List(User)
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * Gets the text of the message
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the date the message was sent
     * @return LocalDateTime
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Sets the user who sent the message
     * @param from User
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Sets the list of users the message is sent to
     * @param to List(User)
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * Sets the text of the message
     * @param text String
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the date the message was sent
     * @param date LocalDateTime
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String idsAsString = "";
        for (User u : to){
            idsAsString += u.getId().toString() + " ";
        }
        String messageAsString = getId() + ". " + from.getFirstName() + " " + from.getLastName() + ": " + text + " -> " + idsAsString;

        if (replyOf.isPresent())
            messageAsString += " replied to message " + replyOf.get().getId();

        return messageAsString;
    }
}
