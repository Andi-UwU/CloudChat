package application.domain;


/**
 * Represents a friend request between two users
 */
public class FriendRequest extends Entity<Tuple<Integer,Integer>> {
    private User userFrom;
    private User userTo;
    private FriendRequestStatus status;

    /**
     * Constructor with known users and unknown status
     * @param userFrom User
     * @param userTo User
     */
    public FriendRequest(User userFrom, User userTo) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.status=FriendRequestStatus.PENDING;
    }

    /**
     * Constructor with known users and status
     * @param userFrom User
     * @param userTo User
     * @param status FriendRequestStatus
     */
    public FriendRequest(User userFrom, User userTo, FriendRequestStatus status) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.status = status;
    }

    /**
     * Constructor with unknown users and known status
     * @param status FriendRequestStatus
     */
    public FriendRequest(FriendRequestStatus status) {
        this.userFrom=null;
        this.userTo=null;
        this.status=status;
    }

    /**
     * Default constructor
     */
    public FriendRequest() {
        this.userFrom=null;
        this.userTo=null;
        this.status=FriendRequestStatus.PENDING;
    }

    /**
     * Returns the user that sent the request
     * @return User
     */
    public User getUserFrom() {
        return userFrom;
    }

    /**
     * Sets the sending user
     * @param userFrom User
     */
    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * Gets the request's recipient
     * @return User
     */
    public User getUserTo() {
        return userTo;
    }

    /**
     * Sets the recipient
     * @param userTo User
     */
    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    /**
     * Gets the status of the request
     * @return FriendRequestStatus
     */
    public FriendRequestStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the request
     * @param status FriendRequestStatus
     */
    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return getId().getLeft() + " -> " + getId().getRight() + " " + status.toString();
    }
}
