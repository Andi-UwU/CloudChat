package application.repository.database;

import application.domain.FriendRequest;
import application.domain.Friendship;
import application.domain.Tuple;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.utils.Pagination;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendshipDataBaseRepository extends DataBaseRepository<Tuple<Integer, Integer>, Friendship> {
    private int pageSize = 20;
    /**
     * Constructor
     * @param url database URL
     * @param username login information for database
     * @param password login information for database
     */
    public FriendshipDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

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

    @Override
    public List<Friendship> getAll() throws RepositoryException {
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
        }catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Friendship add(Friendship entity) throws RepositoryException {
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

    @Override
    public Friendship delete(Tuple<Integer, Integer> id) throws RepositoryException {
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

    @Override
    public Integer size() throws RepositoryException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as count from friendship");
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt("count");
        }catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public List<Friendship> getPage(User u1, User u2, Integer page) throws RepositoryException, IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public Integer getNumberOfPages(User u1, User u2) throws RepositoryException {
        return 0;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }
}
