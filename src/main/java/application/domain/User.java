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
     * userName - unique userName on the network for the user
     */
    private String userName;

    /**
     * passWord - used to login a user
     */
    private String passWord;

    /**
     * Constructor
     * @param firstName String
     * @param lastName String
     */
    public User(String firstName, String lastName, String userName){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    /**
     * Constructor for adding new users
     * @param firstName String
     * @param lastName String
     * @param userName String
     * @param passWord String
     */
    public User(String firstName, String lastName, String userName, String passWord) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
        this.passWord=passWord;
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
     * Gets the userName
     * @return String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the userName
     * @param userName String
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the password
     * @return String
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * Sets the password
     * @param passWord String
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
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