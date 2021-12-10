package application.repository.database;

import application.domain.FriendRequest;
import application.domain.FriendRequestStatus;
import application.domain.Tuple;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDataBaseRepository extends DataBaseRepository<Tuple<Integer, Integer>, FriendRequest> {

    public FriendRequestDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Finds a user in the user database
     * @param id Integer
     * @return User
     * @throws RepositoryException if the user doesn't exist
     * @throws SQLException if the database doesn't exist
     */
    private User findUser(Integer id) throws RepositoryException, SQLException {

        String sql = "SELECT * from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            User user = new User(resultSet.getString("first_name"),
                                 resultSet.getString("last_name"));
            user.setId(id);

            resultSet.close();

            return user;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    @Override
    public FriendRequest find(Tuple<Integer, Integer> id) throws RepositoryException {

        String sql = "SELECT * from friend_requests where id_from = ? and id_to = ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement requestStatement = connection.prepareStatement(sql)) {

            requestStatement.setInt(1, id.getLeft());
            requestStatement.setInt(2, id.getRight());
            ResultSet resultSetRequest = requestStatement.executeQuery();
            resultSetRequest.next();

            User userFrom = findUser(id.getLeft());
            User userTo = findUser(id.getRight());

            FriendRequestStatus status;
            try { status = FriendRequestStatus.valueOf(resultSetRequest.getString("status")); }
            catch (IllegalArgumentException e ) { throw new SQLException("Illegal status from database!\n"); }

            FriendRequest request = new FriendRequest(userFrom,userTo,status);
            request.setId(new Tuple<>(id.getLeft(), id.getRight()));

            resultSetRequest.close();

            return request;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent friend request!\n");
        }
    }

    @Override
    public List<FriendRequest> getAll() throws SQLException, ValidationException, RepositoryException {
        List<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friend_requests");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Integer id_from = resultSet.getInt("id_from");
                Integer id_to = resultSet.getInt("id_to");
                FriendRequestStatus status;
                try { status = FriendRequestStatus.valueOf(resultSet.getString("status")); }
                catch (IllegalArgumentException e ) { throw new SQLException("Illegal status from database!\n"); }

                FriendRequest request = new FriendRequest(findUser(id_from),findUser(id_to),status);
                request.setId(new Tuple<>(id_from,id_to));
                requests.add(request);
            }
            return requests;
        }
    }

    /*
    Unused so far [!]
    Optimization code [!]
    [!] DO NOT DELETE [!]

    private List<FriendRequest> getAllForUser(Integer id) throws SQLException, ValidationException, RepositoryException {
        List<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friend_requests where id_to=id");
             ResultSet resultSet = statement.executeQuery() ) {

            while (resultSet.next()) {
                Integer id_from = resultSet.getInt("id_from");
                Integer id_to = resultSet.getInt("id_to");
                FriendRequestStatus status;
                try { status = FriendRequestStatus.valueOf(resultSet.getString("status")); }
                catch (IllegalArgumentException e ) { throw new SQLException("Illegal status from database!\n"); }

                FriendRequest request = new FriendRequest(findUser(id_from),findUser(id_to),status);
                request.setId(new Tuple<>(id_from,id_to));
                validator.validate(request);
                requests.add(request);
            }
            return requests;
        }
    }

    private List<FriendRequest> getAllFromUser(Integer id) throws SQLException, ValidationException, RepositoryException {
        List<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friend_requests where id_from=id");

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id_from = resultSet.getInt("id_from");
                Integer id_to = resultSet.getInt("id_to");
                FriendRequestStatus status;
                try { status = FriendRequestStatus.valueOf(resultSet.getString("status")); }
                catch (IllegalArgumentException e ) { throw new SQLException("Illegal status from database!\n"); }

                FriendRequest request = new FriendRequest(findUser(id_from),findUser(id_to),status);
                request.setId(new Tuple<>(id_from,id_to));
                validator.validate(request);
                requests.add(request);
            }
            return requests;
        }
    }
    */

    @Override
    public FriendRequest add(FriendRequest entity) throws IOException, RepositoryException {
        String sql = "insert into friend_requests (id_from, id_to, status ) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, entity.getId().getLeft());
            ps.setInt(2, entity.getId().getRight());
            ps.setString(3, entity.getStatus().toString());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RepositoryException("This friend request already exists!\n");
        }
    }

    @Override
    public FriendRequest delete(Tuple<Integer, Integer> id) throws IOException, RepositoryException {
        String sql = "delete from friend_requests where id_from = ? and id_to = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id.getLeft());
            ps.setInt(2, id.getRight());
            FriendRequest deleted = find(id);
            ps.executeUpdate();

            return deleted;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent user!\n");
        }
    }

    @Override
    public FriendRequest update(FriendRequest entity) throws RepositoryException {
        String sql = "update friend_requests set status = ? where id_from = ? and id_to = ?";
        FriendRequest updated;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getStatus().toString());
            ps.setInt(2, entity.getId().getLeft());
            ps.setInt(3, entity.getId().getRight());

            updated = find(entity.getId());
            ps.executeUpdate();

            return updated;
        }
        catch (SQLException throwable) {
            throw new RepositoryException("Nonexistent friend request!\n");
        }
    }

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

