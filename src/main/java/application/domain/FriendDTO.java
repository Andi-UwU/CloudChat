package application.domain;

import javafx.scene.control.CheckBox;

import java.util.Objects;

/**
 * Friend data-transfer-object to UI
 */
public class FriendDTO {
    private Integer id;
    private String name;
    private String date;
    private CheckBox select;

    /**
     * Constructor
     * @param id Integer
     * @param name String
     * @param date String
     */
    public FriendDTO(Integer id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.select = new CheckBox();
    }

    /**
     * Gets object ID
     * @return Integer
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets object ID
     * @param id Integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Constructor
     */
    public FriendDTO() {
        this.select = new CheckBox();
    }

    /**
     * Gets friend name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets friend name
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets friend request accepted date
     * @return String
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the friend requets accepted date
     * @param date String
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets object checkbox
     * @return CheckBox
     */
    public CheckBox getSelect() {
        return select;
    }

    /**
     * Sets the object checkbox
     * @param select CheckBox
     */
    public void setSelect(CheckBox select) {
        this.select = select;
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
