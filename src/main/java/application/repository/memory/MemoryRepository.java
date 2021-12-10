package application.repository.memory;

import application.domain.Entity;
import application.exceptions.RepositoryException;
import application.repository.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    /**
     * entities - a map that stores the entities of the repository
     */
    private Map<ID, E> entities;

    /**
     * constructor
     */
    public MemoryRepository(){
        entities = new HashMap<ID, E>();
    }

    /**
     * finds the entity with the given id
     * @param id
     * @return the entity with the given id if it exists in the repository
     * @return null, if there is no entity with the given id
     */
    @Override
    public E find(ID id) {
        return entities.get(id);
    }

    /**
     * gets all entities of the repository
     * @return all entities as Iterable
     */
    @Override
    public List<E> getAll() {

        return (List<E>) entities.values();
    }

    /**
     *
     * @param entity
     * @return entity, if the entity is already in the repository
     * @return null, if the entity was added to the repository
     */
    @Override
    public E add(E entity) throws IOException, RepositoryException {

        if (entities.get(entity.getId()) != null) // if there is already an object with the same id in the repository
            throw new RepositoryException("An entity with the same id already exists!\n");

        entities.put(entity.getId(), entity);

        return entity;
    }

    /**
     *
     * @param id
     * @return the entity that was removed
     * @return null, if there is no entity with the given id in the repository
     */
    @Override
    public E delete(ID id) throws IOException, RepositoryException {
        E e = entities.remove(id);
        if (e != null)
            return e;
        throw new RepositoryException("The entity does not exist!\n");
    }

    /**
     *
     * @param entity
     * @return the previous entity with the given id
     * @return null, if there is no entity with the given id in the repository
     */
    @Override
    public E update(E entity) throws IOException, RepositoryException {

        E e = entities.replace(entity.getId(), entity);
        if (e != null)
            return e;
        throw new RepositoryException("The entity does not exist!\n");
    }

    /**
     * gets the number of entities from repository
     * @return  size of repository as Integer
     */
    @Override
    public Integer size(){
        return entities.size();
    }
}