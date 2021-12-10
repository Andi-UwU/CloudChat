package application.repository.database;

import application.domain.Friendship;
import application.domain.Tuple;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDataBaseRepository extends DataBaseRepository<Tuple<Integer, Integer>, Friendship> {
    /**
     * constructor
     * @param url of database
     * @param username of the database account
     * @param password of the database account
     */
    public FriendshipDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * finds the entity with the id
     * @param id
     * the entity if it exists in the repository
     * false otherwise
     */
    @Override
    public Friendship find(Tuple<Integer, Integer> id) throws RepositoryException {
        String sql = "SELECT * from friendship where id_left = ? and id_right = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id.getLeft());
            statement.setInt(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));

            Friendship friendship = new Friendship(date);
            friendship.setId(new Tuple<>(id.getLeft(), id.getRight()));

            resultSet.close();

            return friendship;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent friendship!\n");
        }
    }

    /**
     * gets all entities of the repository
     * @return all entities as Iterable
     */
    @Override
    public List<Friendship> getAll() throws SQLException, ValidationException {
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer leftId = resultSet.getInt("id_left");
                Integer rightId = resultSet.getInt("id_right");
                LocalDateTime date = LocalDateTime.parse(resultSet.getString("date"));
                Friendship friendship = new Friendship(date);
                friendship.setId(new Tuple<>(leftId, rightId));
                friendships.add(friendship);
            }
            return friendships;
        }
    }

    /**
     * adds an entity to the repository
     * @param entity added to repository
     * @return
     * entity, if the entity is already in the repository
     * null, if the entity has been added
     *
     * @throws IOException if reading from database fail
     */
    @Override
    public Friendship add(Friendship entity) throws IOException, RepositoryException {
        String sql = "insert into friendship (id_left, id_right, date ) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, entity.getId().getLeft());
            ps.setInt(2, entity.getId().getRight());
            ps.setString(3, entity.getDate().toString());

            ps.executeUpdate();

            return entity;

        } catch (SQLException e) {
            throw new RepositoryException("The friendship already exists!\n");
        }

    }

    /**
     * deletes the entity with the given id
     * @param id of the friendship
     * @return
     * the entity that was deleted, if the entity was in the repository
     * null, if the entity did not exist in the repository
     * @throws IOException if reading from database fails
     */
    @Override
    public Friendship delete(Tuple<Integer, Integer> id) throws IOException, RepositoryException {
        String sql = "delete from friendship where id_left = ? and id_right = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id.getLeft());
            ps.setInt(2, id.getRight());
            Friendship deleted = find(id);
            ps.executeUpdate();

            return deleted;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    /**
     * replace the given entity with the entity from repository with the same id
     * @param entity added
     * @return
     * the entity that was replaced, if there existed an entity with the same id as the given entity
     * null, otherwise
     */
    @Override
    public Friendship update(Friendship entity) throws RepositoryException {
        String sql = "update friendship set date = ? where id_left = ? and id_right = ?";
        Friendship updated;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getDate().toString());
            ps.setInt(2, entity.getId().getLeft());
            ps.setInt(3, entity.getId().getRight());

            updated = find(entity.getId());
            ps.executeUpdate();

            return updated;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent Friendship!\n");
        }
    }

    /**
     * gets the number of entities from repository
     * @return  size of repository as Integer
     */
    @Override
    public Integer size() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as count from friendship");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt("count");
        }
    }
}
