package application.domain;

import java.util.Objects;

/**
 * Add friend data-transfer-object to UI
 */
public class AddFriendDTO {
    private Integer id;
    private String name;
    private String request;

    /**
     * Default constructor
     */
    public AddFriendDTO(){}

    /**
     * Constructor
     * @param id Integer
     * @param name String
     * @param request String
     */
    public AddFriendDTO(Integer id, String name, String request) {
        this.id = id;
        this.name = name;
        this.request = request;
    }

    /**
     * Returns object ID
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
     * Returns name
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets request
     * @return String
     */
    public String getRequest() {
        return request;
    }

    /**
     * Sets request
     * @param request String
     */
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
