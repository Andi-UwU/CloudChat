package application.service;

import application.domain.*;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class SuperService {
    private final Network network;

    private final MessageService messageService;
    private final FriendRequestService friendRequestService;

    public SuperService(Network network, MessageService messageService, FriendRequestService friendRequestService) {
        this.network = network;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
    }


    // ===================== NETWORK ==========================
    /**
     * gets the friend list of the given user
     * @param user from which is extracted the friend list
     * @return the friend list of the user as Iterable
     */
    public List<User> friendList(User user) throws SQLException, RepositoryException, ValidationException {

        return network.friendList(user);
    }

    /**
     * gets a string list of all the friendships the user has
     * @param userId Integer
     * @return List(String)
     * @throws ValidationException if data is not valid
     * @throws SQLException if the database isn't available
     * @throws RepositoryException if a user doesn't exist
     */
    public List<String> getFriendshipsOfUser(Integer userId) throws ValidationException, SQLException, RepositoryException {

        return network.getFriendshipsOfUser(userId);
    }

    /**
     * gets a string list of all the friendships a user has made in a specific month
     * @param userId Integer
     * @param month Integer
     * @return List(String)
     */
    public List<String> getFriendshipsOfUserFromMonth(Integer userId, Integer month) throws ValidationException, SQLException, RepositoryException {
        return network.getFriendshipsOfUserFromMonth(userId, month);
    }

    /**
     * gets the communities number from the network
     * @return communities number as int
     */
    public int getCommunitiesNumber() throws SQLException, ValidationException, RepositoryException {
        return network.getCommunitiesNumber();
    }

    //==================== USERS ==========================

    /**
     * adds a user to the network
     * @param firstName of the user
     * @param lastName of the user
     * @return the user, if there is already a user with the same id
     * @throws ValidationException if the user is not valid
     * @throws IOException if reading from data base fail
     */
    public User addUser(String firstName, String lastName) throws ValidationException, IOException, RepositoryException, SQLException {
        return network.addUser(firstName, lastName);
    }

    /**
     * deletes a user from the network
     * @param id of the user that will be deleted
     * @return the user that was deleted
     * @throws IOException if reading from data base fail
     */
    public User deleteUser(Integer id) throws IOException, RepositoryException, SQLException, ValidationException {

        //delete all messages related to user
        friendRequestService.deleteRequestsOfUser(id);
        User deleted = network.findUser(id);
        messageService.deleteMessagesOfUser(deleted);
        //delete user
        return network.deleteUser(id);
    }

    /**
     * update a user
     * @param id of the user
     * @param firstName new first name
     * @param lastName new last name
     * @return the user that has been replaced
     * @throws ValidationException if the attributes are not valid
     * @throws IOException if reading from data base fail
     */
    public User updateUser(Integer id, String firstName, String lastName) throws ValidationException, IOException, RepositoryException, SQLException {
        return network.updateUser(id, firstName, lastName);
    }

    /**
     * gets all users of the network
     * @return all users as Iterable
     */
    public List<User> getAllUsers() throws SQLException, ValidationException, RepositoryException {
        return network.getAllUsers();
    }

    /**
     * finds a user with the given id
     * @param id of the user
     * @return the found user
     */
    public User findUser(Integer id) throws RepositoryException, ValidationException {
        return network.findUser(id);
    }

    public User loginUser(Integer id, String password) {
        return new User("unused","so far");
    }
    //=================== FRIENDSHIPS =======================


    /**
     * adds a friendship
     * @param leftId id of a user
     * @param rightId id of another user
     * @throws ValidationException if the friendship is not valid
     * @throws RepositoryException if the friendship already exists
     * @throws IOException if reading from data base fails
     */
    public void addFriendship(Integer leftId, Integer rightId) throws ValidationException, RepositoryException, IOException, SQLException {

        network.addFriendship(leftId, rightId);
    }

    /**
     * deletes a friendship
     * @param leftId
     * @param rightId
     * @return the friendship that has been deleted
     * @return null if the friendship does not exist
     * @throws IOException
     */
    public Friendship deleteFriendship(Integer leftId, Integer rightId) throws IOException, RepositoryException, SQLException, ValidationException {
        try {
            friendRequestService.deleteRequest(leftId,rightId);
        }
        catch (RepositoryException ignored) {}
        try {
            friendRequestService.deleteRequest(rightId, leftId);
        }
        catch (RepositoryException ignored) {}
        return network.deleteFriendship(leftId, rightId);
    }

    /**
     * updates a friendship
     * @param leftId
     * @param rightId
     * @param date
     * @return  the friendship that has been updated
     * @return null, if there is no friendship with (leftId, rightId) as id
     * @throws ValidationException
     * @throws IOException
     */
    public Friendship updateFriendship(Integer leftId, Integer rightId, LocalDateTime date) throws ValidationException, IOException, RepositoryException, SQLException {

        return network.updateFriendship(leftId, rightId, date);
    }


    /**
     * finds a friendship
     * @param leftId
     * @param rightId
     * @return  friendship with the (leftId, rightId) as id
     * @return null if there is no friendship with the (leftId, rightId) as id
     */
    public Friendship findFriendship(Integer leftId, Integer rightId) throws SQLException, RepositoryException, ValidationException {

        return network.findFriendship(leftId, rightId);
    }

    /**
     * gets all friendships from the repository
     * @return  all friendship as Iterable
     */
    public List<Friendship> getAllFriendship() throws SQLException, ValidationException, RepositoryException {
        return network.getAllFriendship();
    }

    // ===================== MESSAGE ==========================


    public List<Message> getAllMessages() throws ValidationException, SQLException, RepositoryException {
        return messageService.getAll();
    }

    public Message findMessage(Integer id) throws ValidationException, SQLException, RepositoryException {
        return messageService.find(id);
    }

    public void addMessage(Integer fromId, List<Integer> toIds, String text) throws ValidationException, SQLException, RepositoryException, IOException {
        User from = network.findUser(fromId);
        List<User> to = new ArrayList<>();
        for(Integer id : toIds){
            to.add(network.findUser(id));
        }

        messageService.addMessage(from, to, text);
    }

    public void addReply(Integer fromId, Integer replyToId, String text) throws ValidationException, SQLException, RepositoryException, IOException {
        User from = network.findUser(fromId);

        Message replyTo = messageService.find(replyToId);

        messageService.addReply(from, text, replyTo);
    }

    public Message updateMessage(Integer messageId, String newText) throws ValidationException, SQLException, RepositoryException, IOException {
        Message message = messageService.find(messageId);
        return messageService.update(messageId, message.getTo(), newText);
    }

    public Message deleteMessage(Integer messageId) throws ValidationException, SQLException, RepositoryException, IOException {
        return messageService.delete(messageId);
    }

    public Message userDeleteMessage(User user, Integer messageId) throws ValidationException, SQLException, RepositoryException, ServiceException, IOException {
        Message message = findMessage(messageId);
        if (!message.getFrom().equals(user))
            throw new ServiceException("This message does not belong to you!\n");

        deleteMessage(messageId);

        return message;
    }

    public Integer getMessagesSize() throws SQLException {
        return messageService.size();
    }

    public List<Message> getConversation(Integer userId1, Integer userId2) throws ValidationException, SQLException, RepositoryException {
        List<Message> messages = messageService.getAll();
        User user1 = network.findUser(userId1);
        User user2 = network.findUser(userId2);
        List<Message> conversation = messages
                .stream()
                .filter(m -> (m.getFrom().equals(user1) && m.getTo().contains(user2))
                        || (m.getFrom().equals(user2) && m.getTo().contains(user1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
        return conversation;
    }

    // ===================== FRIEND REQUEST ==========================

    public Iterable<FriendRequest> getAllFriendRequests() throws ValidationException, SQLException, RepositoryException {
        return friendRequestService.getAll();
    }

    public Iterable<FriendRequest> getAllFriendRequestsForUser(Integer id) throws ValidationException, SQLException, RepositoryException {
        findUser(id);
        return friendRequestService.getAllToUser(id);
    }

    public Iterable <FriendRequest> getAllFriendRequestsFromUser(Integer id) throws ValidationException, SQLException, RepositoryException {
        findUser(id);
        return friendRequestService.getAllFromUser(id);
    }

    public void addFriendRequest(Integer idFrom, Integer idTo) throws ValidationException, SQLException, RepositoryException, IOException {
        User userFrom = findUser(idFrom);
        User userTo = findUser(idTo);

        try { // if request already exists
            FriendRequest oldRequest = friendRequestService.findRequest(idFrom, idTo);
            if (oldRequest.getStatus().toString().equals("DECLINED")) { // remove declined request
                friendRequestService.deleteRequest(idFrom,idTo);
                FriendRequest newRequest = new FriendRequest();
                newRequest.setId(new Tuple<>(idFrom,idTo));
                friendRequestService.addRequest(newRequest); // add new pending request
            }
            return;
        }
        catch (RepositoryException ignored) { }

        try {
            findFriendship(idFrom, idTo);
            throw new ValidationException("Friendship already exists!\n");
        } catch (RepositoryException ignored) {
        }

        try { // see if friend already sent a friend request
            FriendRequest oldRequest = friendRequestService.findRequest(idTo, idFrom); // request from friend exists
            if (oldRequest.getStatus().toString().equals("PENDING")) { // old request is PENDING
                oldRequest.setStatus(FriendRequestStatus.ACCEPTED);
                friendRequestService.updateRequest(idTo, idFrom, "ACCEPTED");
                network.addFriendship(idFrom, idTo);
            } else { // old request is DECLINED
                friendRequestService.deleteRequest(idTo, idFrom);
                FriendRequest newRequest = new FriendRequest(userFrom, userTo);
                newRequest.setId(new Tuple<>(idFrom, idTo));
                friendRequestService.addRequest(newRequest);
            }
            return;

        } catch (RepositoryException e) { // no previous friend requests exist
            FriendRequest newRequest = new FriendRequest(userFrom, userTo);
            newRequest.setId(new Tuple<>(idFrom, idTo));
            friendRequestService.addRequest(newRequest);
            return;
        }
    }

    public FriendRequest updateFriendRequest(Integer idFrom,Integer idTo, String status) throws ValidationException, SQLException, RepositoryException, IOException {
        FriendRequest request = friendRequestService.updateRequest(idFrom,idTo,status);
        if (status.equals("ACCEPTED")) {
            addFriendship(idFrom,idTo);
        }
        return request;
    }

    public FriendRequest deleteFriendRequest(Integer idFrom, Integer idTo) throws ValidationException, SQLException, RepositoryException, IOException {
        return friendRequestService.deleteRequest(idFrom,idTo);
    }
}
