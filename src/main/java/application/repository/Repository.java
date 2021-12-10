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
     * @return: generic type E
     * @throws RepositoryException if the entity doesn't exist
     * @throws ValidationException if the entity is invalid (probable improper storage in repository)
     * @throws SQLException if a database cannot be reached
     */
    E find(ID id) throws RepositoryException, ValidationException;

    /**
     * Gets all the entities of a repository
     * @return List(of generic type E)
     * @throws SQLException if the database cannot be reached
     * @throws ValidationException if the entity is invalid (probable improper storage in repository)
     * @throws RepositoryException if a database has a foreign key towards a nonexistent value
     */
    List<E> getAll() throws SQLException, ValidationException, RepositoryException;

    /**
     * Adds an entity to the repository
     * @return the entity added
     * @param entity generic type E
     * @throws RepositoryException if another entity with the same ID already exists in the repository
     * @throws IOException if the entity cannot be parsed
     */
    E add(E entity) throws IOException, RepositoryException;

    /**
     * Deletes the entity with the given id and returns the old value
     * @param id generic type ID
     * @return generic type E
     * @throws RepositoryException if the entity with the provided id cannot be found
     * @throws ValidationException if the entity is invalid (improper storage in repository)
     * @throws SQLException if the database cannot be reached
     * @throws IOException if the entity cannot be parsed
     */
    E delete(ID id) throws IOException, RepositoryException, SQLException, ValidationException;

    /**
     * Replace an entity with another that has the same id and returns the old value
     * @param entity generic type E
     * @return generic type E
     * @throws RepositoryException if an entity with that id doesn't exist
     * @throws SQLException if the database cannot be reached
     * @throws ValidationException if the entity is invalid (improper storage in repository)
     * @throws IOException if the entity cannot be parsed
     */
    E update(E entity) throws IOException, RepositoryException, SQLException, ValidationException;

    /**
     * Gets the size of the repository
     * @return Integer
     * @throws SQLException if the database cannot be reached
     */
    Integer size() throws SQLException;
}