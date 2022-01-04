package application.repository;

import application.domain.Entity;

import java.util.List;

public interface FriendRequestRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    List<E> getAllFromUser(Integer id);

    /*
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship WHERE id_left = ?");
             statement.setInt(1, id);
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
     */
    List<E> getAllToUser(Integer id);
    /*
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship WHERE id_right = ?");
             statement.setInt(1, id);
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
     */
}
