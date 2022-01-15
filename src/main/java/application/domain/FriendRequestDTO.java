package application.domain;


import java.util.Objects;


/**
 * Friend request data-transfer-object
 */
public class FriendRequestDTO {
    private String nameFrom;
    private String nameTo;
    private int idFrom;
    private int idTo;
    private FriendRequestStatus status;

    public FriendRequestDTO(String nameFrom, String nameTo, int idFrom, int idTo, FriendRequestStatus status) {
        this.nameFrom = nameFrom;
        this.nameTo = nameTo;
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.status = status;
    }

    /**
     * Gets name from friend request sender
     * @return String
     */
    public String getNameFrom() {
        return nameFrom;
    }

    /**
     * Sets name for friend request sender
     * @param nameFrom String
     */
    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    /**
     * Gets name from friend request receiver
     * @return String
     */
    public String getNameTo() {
        return nameTo;
    }

    /**
     * Sets name for friend request receiver
     * @param nameTo
     */
    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    /**
     * Gets ID from friend request sender
     * @return int
     */
    public int getIdFrom() {
        return idFrom;
    }

    /**
     * Sets ID for friend request sender
     * @param idFrom
     */
    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    /**
     * Gets ID from friend request receiver
     * @return
     */
    public int getIdTo() {
        return idTo;
    }

    /**
     * Sets ID for friend request receiver
     * @param idTo int
     */
    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }

    /**
     * Gets friend request status
     * @return FriendRequestStatus
     */
    public FriendRequestStatus getStatus() {
        return status;
    }

    /**
     * Sets friend request status
     * @param status FriendRequestStatus
     */
    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequestDTO that = (FriendRequestDTO) o;
        return idFrom == that.idFrom && idTo == that.idTo &&
                nameFrom.equals(that.nameFrom) &&
                nameTo.equals(that.nameTo) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameFrom, nameTo, idFrom, idTo, status);
    }
}
