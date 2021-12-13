package application.domain;

import java.util.Objects;

public class FriendDTO {
    private Integer id;
    private String name;
    private String date;

    public FriendDTO(Integer id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
    //TODO these comments

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FriendDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendDTO friendDTO = (FriendDTO) o;
        return Objects.equals(id, friendDTO.id) && Objects.equals(name, friendDTO.name) && Objects.equals(date, friendDTO.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date);
    }
}
