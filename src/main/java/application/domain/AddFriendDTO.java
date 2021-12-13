package application.domain;

import java.util.Objects;

public class AddFriendDTO {
    private Integer id;
    private String name;
    private String request;

    public AddFriendDTO(){
    }

    public AddFriendDTO(Integer id, String name, String request) {
        this.id = id;
        this.name = name;
        this.request = request;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddFriendDTO that = (AddFriendDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, request);
    }
}
