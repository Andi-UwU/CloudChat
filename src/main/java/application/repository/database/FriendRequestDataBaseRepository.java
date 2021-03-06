package application.repository.database;

import application.domain.*;
import application.exceptions.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendRequestDataBaseRepository extends DataBaseRepository<Tuple<Integer, Integer>, FriendRequest> {
    private int pageSize = 20;

    public FriendRequestDataBaseRepository(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public FriendRequest find(Tuple<Integer, Integer> id) throws RepositoryException {

        String sql = "SELECT u1.id, u1.first_name, u1.last_name, u1.username, u2.id, u2.first_name, u2.last_name,u2.username,status FROM friend_request fr INNER JOIN user u1 on fr.id_from = u1.id  INNER JOIN user u2 on fr.id_to = u2.id  WHERE fr.id_from=? AND fr.id_to=?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement requestStatement = connection.prepareStatement(sql)) {

            requestStatement.setInt(1, id.getLeft());
            requestStatement.setInt(2, id.getRight());
            ResultSet resultSetRequest = requestStatement.executeQuery();

            resultSetRequest.next();

            User userFrom = new User(   resultSetRequest.getString(2),
                                        resultSetRequest.getString(3),
                                        resultSetRequest.getString(4));
            User userTo = new User( resultSetRequest.getString(6),
                                    resultSetRequest.getString(7),
                                    resultSetRequest.getString(8));

            userFrom.setId( resultSetRequest.getInt(1));
            userTo.setId( resultSetRequest.getInt(5));

            FriendRequestStatus status = FriendRequestStatus.valueOf
                    ( resultSetRequest.getString(9) );

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
    public List<FriendRequest> getAll() throws RepositoryException {
        List<FriendRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
              "SELECT u1.id, u1.first_name, u1.last_name,u1.username, u2.id, u2.first_name, u2.last_name, u2.username, status FROM friend_request fr INNER JOIN \"user\" u1 on fr.id_from = u1.id INNER JOIN \"user\" u2 on fr.id_to = u2.id ");

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User userFrom = new User(   resultSet.getString(2),
                                            resultSet.getString(3),
                                            resultSet.getString(4));
                User userTo = new User (    resultSet.getString(6),
                                            resultSet.getString(7),
                                            resultSet.getString(8));

                userFrom.setId( resultSet.getInt(1));
                userTo.setId( resultSet.getInt(5));

                FriendRequestStatus status = FriendRequestStatus.valueOf
                        ( resultSet.getString(9) );

                FriendRequest request = new FriendRequest(userFrom,userTo,status);
                request.setId(new Tuple<>(userFrom.getId(), userTo.getId()));
                requests.add(request);
            }
            return requests;
        }catch (SQLException e){
            throw new RepositoryException(e.getMessage());
        }

    }

    public List<FriendRequest> getAllFromUser() {
        return null;
    }

    @Override
    public FriendRequest add(FriendRequest entity) throws RepositoryException {
        String sql = "insert into friend_request (id_from, id_to, status ) values (?, ?, ?)";
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
    public FriendRequest delete(Tuple<Integer, Integer> id) throws RepositoryException {
        String sql = "delete from friend_request where id_from = ? and id_to = ?";

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
        String sql = "update friend_request set status = ? where id_from = ? and id_to = ?";
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
    public List<FriendRequest> getPage(User u1, User u2, Integer page) throws RepositoryException, IllegalArgumentException {
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

