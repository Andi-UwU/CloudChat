package application.repository.database;

import application.domain.Entity;
import application.repository.PagingRepository;
import application.repository.Repository;

/**
 * @param <ID> generic type, defines the type of entity ids used
 * @param <E> generic type, defines the type of entity used
 */
public abstract class DataBaseRepository<ID, E extends Entity<ID>> implements Repository<ID, E>, PagingRepository<ID,E> {
    protected String url; // url of the database
    protected String username; // the username of the postgres server
    protected String password; // the password of the user

    /**
     * Constructor
     * @param url String
     * @param username String
     * @param password String
     */
    public DataBaseRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
