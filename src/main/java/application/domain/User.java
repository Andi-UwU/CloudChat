package application.domain;

import java.util.Objects;

/**
 * Represents a user
 */
public class User extends Entity<Integer>{

    /**
     * firstName - first name of the user
     */
    private String firstName;
    /**
     * secondName - second name of the user
     */
    private String lastName;

    /**
     * Constructor
     * @param firstName String
     * @param lastName String
     */
    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the first name
     * @return String
     */
    public String getFirstName(){
        return firstName;
    }

    /**
     * Gets the last name
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the first name
     * @param firstName String
     */
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    /**
     * Sets the last name
     * @param lastName String
     */
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    /**
     * Test if two users are equal
     * @param o User
     * @return true, if the user is equal to o ; false, otherwise
     */
    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof User user))
            return false;

        return getFirstName().equals(user.getFirstName())&&
                getLastName().equals(user.getLastName())&&
                getId().equals(user.getId());

    }

    /**
     * @return int - the hashCode of the user
     */
    @Override
    public int hashCode(){
        return Objects.hash(getFirstName(), getLastName(), getId());
    }

    /**
     * @return String (of user)
     */
    @Override
    public String toString() {
        return  getId() + ". " + firstName + " " + lastName;
    }
}