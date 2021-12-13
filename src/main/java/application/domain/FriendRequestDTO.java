package application.domain;


import java.util.Objects;


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

    //TODO these comments

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public int getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    public int getIdTo() {
        return idTo;
    }

    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

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
