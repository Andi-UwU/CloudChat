package application.repository;

import application.domain.Entity;
import application.domain.User;
import application.exceptions.RepositoryException;

import java.util.List;

public interface PagingRepository<ID, E extends Entity<ID>>  {
    List<E> getPage(User u1, User u2, Integer page) throws RepositoryException, IllegalArgumentException;

    Integer getNumberOfPages(User u1, User u2) throws RepositoryException;

    Integer getPageSize();
}
