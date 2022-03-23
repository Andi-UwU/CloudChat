package application.repository.database;

import application.domain.User;
import application.exceptions.RepositoryException;

import de.mkammerer.argon2.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDataBaseRepository extends DataBaseRepository<Integer, User> {
    private int pageSize = 20;

    /**
     * Constructor for database
     * @param url database postgres url
     * @param username database login
     * @param password database login
     */
    public UserDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Used to authenticate a user logging in the application
     * @param userName the user's provided username
     * @param passWord the user's provided password
     * @return int user's ID or -1 if it failed
     * @throws RepositoryException if the params are invalid
     */
    public int login(String userName, String passWord) throws RepositoryException {
        String sql = "SELECT id,hash from \"user\" where username = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            String hash = resultSet.getString("hash");
            int id = resultSet.getInt("id");

            Argon2 argon2 = Argon2Factory.create(16, 32);

            if (argon2.verify(hash,passWord))
                return id;
            else return -1;
        }
        catch (SQLException throwable){
            throw new RepositoryException("Invalid login information!\n");
        }
    }

    @Override
    public User find(Integer id) throws RepositoryException {

        String sql = "SELECT id,first_name,last_name,username from \"user\" where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
             ){

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            Integer id1 = resultSet.getInt("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String userName = resultSet.getString("username");
            User user = new User(firstName, lastName, userName);
            user.setId(id1);

            resultSet.close();
            return user;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    @Override
    public List<User> getAll() throws RepositoryException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id,first_name,last_name,username from \"user\"");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userName = resultSet.getString("username");
                User user = new User(firstName, lastName, userName);
                user.setId(id);
                users.add(user);
            }
            return users;
        }catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public User add(User entity) throws RepositoryException {
        String sql = "insert into \"user\" (first_name, last_name, username, hash) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUserName());
            ps.setString(4,entity.getPassWord());

            ps.executeUpdate();

            return entity;

        } catch (SQLException e) {
            throw new RepositoryException("This username already exists!\n");
        }
    }

    @Override
    public User delete(Integer id) throws RepositoryException {

        String sql = "delete from \"user\" where id = ?";

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

    @Override
    public User update(User entity) throws RepositoryException {
        String sql = "update \"user\" set first_name = ?, last_name = ? where username = ?";
        User updated;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUserName());
            updated = find(entity.getId());
            ps.executeUpdate();

            return updated;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    @Override
    public Integer size() throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as count from \"user\"");
             ResultSet resultSet = statement.executeQuery()) {

                resultSet.next();
                return resultSet.getInt("count");
        }catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public List<User> getPage(User u1, User u2, Integer page) throws RepositoryException, IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public Integer getNumberOfPages(User u1, User u2) throws RepositoryException {
        return 1;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }
}
