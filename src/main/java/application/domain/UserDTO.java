package application.domain;

import javafx.scene.control.CheckBox;

import java.util.Objects;

/**
 * User data-tranfer-object to UI
 */
public class UserDTO {

    private Integer id;
    private String name;

    /**
     * Constructor
     * @param id Integer
     * @param name String
     */
    public UserDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Default constructor
     */
    public UserDTO() {}

    /**
     * Gets the user's ID
     * @return Integer
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the user's ID
     * @param id Integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the user's name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name
     * @param name String
     */
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
