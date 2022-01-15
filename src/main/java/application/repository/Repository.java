package application.repository;

import application.domain.Entity;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds the entity with a specific id
     * @param id generic type ID
     * @return generic type E
     * @throws RepositoryException if the entity doesn't exist
     */
    E find(ID id) throws RepositoryException;

    /**
     * Gets all the entities of a repository
     * @return List(of generic type E)
     * @throws SQLException if the database cannot be reached
     * @throws RepositoryException if a database has a foreign key towards a nonexistent value
     */
    List<E> getAll() throws RepositoryException;

    /**
     * Adds an entity to the repository
     * @return the entity added
     * @param entity generic type E
     * @throws RepositoryException if another entity with the same ID already exists in the repository
     */
    E add(E entity) throws RepositoryException;

    /**
     * Deletes the entity with the given id and returns the old value
     * @param id generic type ID
     * @return generic type E
     * @throws RepositoryException if the entity with the provided id cannot be found
     */
    E delete(ID id) throws RepositoryException;

    /**
     * Replace an entity with another that has the same id and returns the old value
     * @param entity generic type E
     * @return generic type E
     * @throws RepositoryException if an entity with that id doesn't exist
     */
    E update(E entity) throws RepositoryException;

    /**
     * Gets the size of the repository
     * @return Integer
     * @throws RepositoryException if the database cannot be reached
     */
    Integer size() throws  RepositoryException;

}