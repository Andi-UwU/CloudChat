package application.domain;

public class Entity<ID> {
    /**
     * generic ID of an entity
     */
    private ID id;

    /**
     * Generic entity constructor
     * @param id generic type ID
     */
    public Entity(ID id) {
        this.id = id;
    }

    /**
     * Default entity constructor
     */
    public Entity() {
    }

    /**
     * Returns the id of a generic entity
     * @return generic type ID
     */
    public ID getId(){
        return id;
    }

    /**
     * Sets the id of a generic entity
     * @param id generic type ID
     */
    public void setId(ID id){
        this.id = id;
    }

}
