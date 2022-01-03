package application.domain;

import javafx.scene.control.CheckBox;

import java.util.Objects;

public class UserDTO {

    private Integer id;
    private String name;

    public UserDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDTO() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return id.equals(userDTO.id) && name.equals(userDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
