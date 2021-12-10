package application.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import static application.utils.Constants.DATE_TIME_FORMATTER;

/**
 * Represents the friendship between two users
 */
public class Friendship extends Entity<Tuple<Integer, Integer>>{

    /**
     * date - the date the friendship was created
     */
    private LocalDateTime date;

    private User userLeft;
    private User userRight;

    /**
     * Constructor with known users and unknown date
     * @param userLeft User
     * @param userRight User
     */
    public Friendship(User userLeft, User userRight){
        this.userLeft = userLeft;
        this.userRight = userRight;
    }
    /**
     * Default constructor, unknown users but sets date as current date
     */
    public Friendship() {
        date = LocalDateTime.now();
    }

    /**
     * Gets the first user
     * @return User
     */
    public User getUserLeft() {
        return userLeft;
    }

    /**
     * Constructor with unknown users but known date
     * @param date LocalDateTime
     */
    public Friendship(LocalDateTime date){
        this.date = date;
    }

    /**
     * Sets the first user
     * @param userLeft User
     */
    public void setUserLeft(User userLeft) {
        this.userLeft = userLeft;
    }

    /**
     * Gets the second user
     * @return User
     */
    public User getUserRight() {
        return userRight;
    }

    /**
     * Sets the second user
     * @param userRight User
     */
    public void setUserRight(User userRight) {
        this.userRight = userRight;
    }

    /**
     * Gets the date the friendship was created
     * @return LocalDateTime
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Sets the date
     * @param date LocalDateTime
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return getId().getLeft() + " - " + getId().getRight() + " Date: " + date.format(DATE_TIME_FORMATTER);
    }

    /**
     * Compares if the friendship is equal to another
     * @param o Friendship (other)
     * @return
     * true if this object is equal to o ;
     * false otherwise
     */
    @Override
    public boolean equals (Object o){
        if (!(o instanceof Friendship))
            return false;
        return getId().equals(((Friendship) o).getId());
    }

    /**
     * @return int - hashCode of object
     */
    @Override
    public int hashCode(){
        return Objects.hash(getId().getLeft(), getId().getRight());
    }
}
