package application.repository;

import application.domain.Entity;
import application.domain.User;
import application.exceptions.RepositoryException;

import java.util.List;

/**
 * Interface for paging repositories
 * @param <ID> generic type, defines entity IDs
 * @param <E> generic type, defines entity types
 */
public interface PagingRepository<ID, E extends Entity<ID>>  {

    /**
     * Gets a page from the repository
     * @param u1 User
     * @param u2 User
     * @param page Integer
     * @return List(E)
     * @throws RepositoryException
     */
    List<E> getPage(User u1, User u2, Integer page) throws RepositoryException;

    /**
     * Returns the number of pages available
     * @param u1 User
     * @param u2 User
     * @return Integer
     * @throws RepositoryException
     */
    Integer getNumberOfPages(User u1, User u2) throws RepositoryException;

    /**
     * Returns the size of a repository's page
     * @return Integer
     */
    Integer getPageSize();
}
