package application.repository.database;

import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDataBaseRepository extends DataBaseRepository<Integer, User>{

    /**
     * constructor
     * @param url of database
     * @param username of the database account
     * @param password of the database account
     */
    public UserDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * finds the entity with the id
     * @param id of user
     * @return the entity if it exists in the repository
     */
    @Override
    public User find(Integer id) throws RepositoryException, ValidationException {

        String sql = "SELECT * from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
             ){

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            Integer id1 = resultSet.getInt("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            User user = new User(firstName, lastName);
            user.setId(id1);

            resultSet.close();

            return user;

        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    /**
     * gets all entities of the repository
     * @return all entities as Iterable
     */
    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        }
    }

    /**
     * adds an entity to the repository
     * @param entity
     * @return
     * entity, if the entity is already in the repository
     * null, if the entity has been added
     *
     * @throws IOException
     */
    @Override
    public User add(User entity) throws IOException, RepositoryException {
        String sql = "insert into users (id, first_name, last_name ) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, entity.getId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.executeUpdate();

            return entity;

        } catch (SQLException e) {
            throw new RepositoryException("The user already exists!\n");
        }
    }

    /**
     * deletes the entity with the given id
     * @param id
     * @return
     * the entity that was deleted, if the entity was in the repository
     * null, if the entity did not exist in the repository
     */
    @Override
    public User delete(Integer id) throws RepositoryException, ValidationException {

        String sql = "delete from users where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            User deleted = find(id);
            ps.executeUpdate();

            return deleted;
        } catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    /**
     * replace the given entity with the entity from repository with the same id
     * @param entity
     * @return
     * the entity that was replaced, if there existed an entity with the same id as the given entity
     * null, otherwise
     */
    @Override
    public User update(User entity) throws RepositoryException, ValidationException {
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        User updated;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setInt(3, entity.getId());
            updated = find(entity.getId());
            ps.executeUpdate();

            return updated;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    /**
     * gets the number of entities from repository
     * @return  size of repository as Integer
     */
    @Override
    public Integer size() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as count from users");
             ResultSet resultSet = statement.executeQuery()) {

                resultSet.next();
                return resultSet.getInt("count");
        }

    }
}
