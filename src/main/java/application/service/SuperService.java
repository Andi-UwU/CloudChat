package application.service;

import application.domain.*;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;
import application.utils.WarningBox;
import application.utils.observer.Observer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static application.utils.Constants.DATE_TIME_FORMATTER;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.COURIER;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.TIMES_ROMAN;

public class SuperService {
    private final Network network;

    private final MessageService messageService;
    private final FriendRequestService friendRequestService;

    public SuperService(Network network, MessageService messageService, FriendRequestService friendRequestService) {
        this.network = network;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
    }

    //TODO some comments down there

    // ===================== NETWORK ==========================
    /**
     * gets the friend list of the given user
     * @param user from which is extracted the friend list
     * @return the friend list of the user as Iterable
     */
    public List<User> friendList(User user) throws RepositoryException, SQLException {
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
    public List<String> getFriendshipsOfUser(Integer userId) throws SQLException, RepositoryException {
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

    public List<FriendDTO> getFriendDtoOfUser(Integer id) throws RepositoryException, SQLException, ValidationException {
        return network.getFriendDtoOfUser(id);
    }

    public List<UserDTO> getUserDtoOfMessage(Message message){
        return message.getTo()
                .stream()
                .map(user -> {
                    Integer id = user.getId();
                    String name = user.getFirstName() + " " + user.getLastName();
                    UserDTO userDTO = new UserDTO(id, name);
                    return userDTO;
                })
                .collect(Collectors.toList());
    }


    public List<User> getNonFriendOfUser(Integer id) throws RepositoryException, SQLException {
        List<User> friendsOfUser = friendList(network.findUser(id));
        return getAllUsers()
                .stream()
                .filter(user -> !(friendsOfUser.contains(user)) && !user.getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<AddFriendDTO> getAddFriendDtoOfUser(Integer id) throws ValidationException, SQLException, RepositoryException {

        List<Integer> pendingRequests = friendRequestService.getAllFromUser(id)
                .stream()
                .filter(request -> request.getStatus().equals(FriendRequestStatus.PENDING))
                .map(request -> request.getUserTo().getId())
                .collect(Collectors.toList());


        return getNonFriendOfUser(id)
                .stream()
                .map(user -> {
                    Integer userId = user.getId();
                    String name = user.getFirstName() + " " + user.getLastName();
                    String request;

                    if (pendingRequests.contains(userId))
                        request = "Already sent";
                    else
                        request = "Send";

                    return new AddFriendDTO(userId, name, request);

                })
                .collect(Collectors.toList());
    }

    public List<AddFriendDTO> getAddFriendDtoOfUserByName(Integer id, String name) throws ValidationException, SQLException, RepositoryException {
        List<Integer> pendingRequests = friendRequestService.getAllFromUser(id)
                .stream()
                .filter(request -> request.getStatus().equals(FriendRequestStatus.PENDING))
                .map(request -> request.getUserTo().getId())
                .collect(Collectors.toList());


        return getNonFriendOfUser(id)
                .stream()
                .filter(user -> {
                    String userName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                    return userName.contains(name.toLowerCase());
                })
                .map(user -> {
                    Integer userId = user.getId();
                    String userName = user.getFirstName() + " " + user.getLastName();
                    String request;

                    if (pendingRequests.contains(userId))
                        request = "Already sent";
                    else
                        request = "Send";

                    return new AddFriendDTO(userId, userName, request);

                })
                .collect(Collectors.toList());
    }

    //==================== USERS ==========================

    /**
     * Adds a user to the database
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param userName username used by the user
     * @param passWord password of the user
     * @return User type object
     * @throws ValidationException if the params are invalid
     * @throws RepositoryException if a user with that username already exists
     */
    public User addUser(String firstName, String lastName, String userName, String passWord) throws ValidationException, RepositoryException {
        return network.addUser(new User(firstName,lastName,userName,passWord));
    }

    /**
     * deletes a user from the network
     * @param id of the user that will be deleted
     * @return the user that was deleted
     * @throws IOException if reading from data base fail
     */
    public User deleteUser(Integer id) throws IOException, RepositoryException, SQLException, ValidationException {

        friendRequestService.deleteRequestsOfUser(id);  // delete all friend requests
        User deleted = network.findUser(id);            // delete user
        messageService.deleteMessagesOfUser(deleted);   // delete all messages related to user
        return network.deleteUser(id);
    }

    public User updateUser(String firstName, String lastName, String userName) throws ValidationException, RepositoryException {
        return network.updateUser(new User(firstName,lastName,userName));
    }

    /**
     * gets all users of the network
     * @return all users as Iterable
     */
    public List<User> getAllUsers() throws SQLException {
        return network.getAllUsers();
    }

    /**
     * finds a user with the given id
     * @param id of the user
     * @return the found user
     */
    public User findUser(Integer id) throws RepositoryException {
        return network.findUser(id);
    }

    public int loginUser(String username, String password) throws RepositoryException {
        return network.loginUser(username, password);
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
    public void addFriendship(Integer leftId, Integer rightId) throws ValidationException, RepositoryException {
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
    public Friendship deleteFriendship(Integer leftId, Integer rightId) throws RepositoryException, SQLException, ValidationException {
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
    public Friendship updateFriendship(Integer leftId, Integer rightId, LocalDateTime date) throws ValidationException, RepositoryException, SQLException {

        return network.updateFriendship(leftId, rightId, date);
    }


    /**
     * finds a friendship
     * @param leftId
     * @param rightId
     * @return  friendship with the (leftId, rightId) as id
     * @return null if there is no friendship with the (leftId, rightId) as id
     */
    public Friendship findFriendship(Integer leftId, Integer rightId) throws RepositoryException {

        return network.findFriendship(leftId, rightId);
    }

    /**
     * gets all friendships from the repository
     * @return  all friendship as Iterable
     */
    public List<Friendship> getAllFriendship() throws SQLException, RepositoryException {
        return network.getAllFriendship();
    }

    // ===================== MESSAGE ==========================


    public List<Message> getAllMessages() throws SQLException, RepositoryException {
        return messageService.getAll();
    }

    public Message findMessage(Integer id) throws ValidationException, SQLException, RepositoryException {
        return messageService.find(id);
    }

    public Message addMessage(Integer fromId, List<Integer> toIds, String text) throws ValidationException, SQLException, RepositoryException, IOException {
        User from = network.findUser(fromId);
        List<User> to = new ArrayList<>();
        for(Integer id : toIds){
            to.add(network.findUser(id));
        }

        return messageService.addMessage(from, to, text);
    }

    public void addReply(Integer fromId, Integer replyToId, String text) throws ValidationException, SQLException, RepositoryException, IOException {
        User from = network.findUser(fromId);
        Message replyTo = messageService.find(replyToId);
        messageService.addReply(from, text, replyTo);
    }

    public void addReplyToAll(Integer fromId, Integer replyToId, String text) throws RepositoryException, ValidationException, SQLException, IOException {
        User from = network.findUser(fromId);
        Message replyTo = messageService.find(replyToId);
        messageService.addReplyToAll(from,text,replyTo);
    }

    public Message updateMessage(Integer messageId, String newText) throws ValidationException, RepositoryException {
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

    public List<Message> getConversation(User user1, User user2) throws RepositoryException {

        return messageService.getConversation(user1, user2);
    }

    // ===================== FRIEND REQUEST ==========================

    public List<FriendRequestDTO> getAllFriendRequestsDtoForUser(Integer id ) throws RepositoryException, SQLException {
        //User user=findUser(id);
        List<FriendRequestDTO> list1 = getAllFriendRequestsFromUser(id)
                .stream()
                .map(request ->{
                    return new FriendRequestDTO(
                            "You",
                            request.getUserTo().getFirstName() +" "+ request.getUserTo().getLastName(),
                            id,
                            request.getUserTo().getId(),
                            request.getStatus()
                            );
                })
                .collect(Collectors.toList());

        List<FriendRequestDTO> list2 = getAllFriendRequestsForUser(id)
                .stream()
                .map(request ->{
                    return new FriendRequestDTO(
                            request.getUserFrom().getFirstName() +" "+ request.getUserFrom().getLastName(),
                            "You",
                            request.getUserFrom().getId(),
                            id,
                            request.getStatus()
                    );
                })
                .collect(Collectors.toList());

        return Stream.concat(list1.stream(),list2.stream()).collect(Collectors.toList());
    }

    public List<FriendRequest> getAllFriendRequests() throws SQLException, RepositoryException {
        return friendRequestService.getAll();
    }

    public List<FriendRequest> getAllFriendRequestsForUser(Integer id) throws SQLException, RepositoryException {
        findUser(id);
        return friendRequestService.getAllToUser(id);
    }

    public List <FriendRequest> getAllFriendRequestsFromUser(Integer id) throws SQLException, RepositoryException {
        findUser(id);
        return friendRequestService.getAllFromUser(id);
    }

    public void addFriendRequest(Integer idFrom, Integer idTo) throws ValidationException, RepositoryException {
        User userFrom = findUser(idFrom);
        User userTo = findUser(idTo);

        try { // if request between users already exists
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

        try { // see if friendship between users exists
            findFriendship(idFrom, idTo);
            throw new ValidationException("Friendship already exists!\n");
        } catch (RepositoryException ignored) { }

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

    public FriendRequest updateFriendRequest(Integer idFrom,Integer idTo, String status) throws ValidationException, RepositoryException {
        FriendRequest request = friendRequestService.updateRequest(idFrom,idTo,status);
        if (status.equals("ACCEPTED")) {
            addFriendship(idFrom,idTo);
        }
        return request;
    }

    public FriendRequest deleteFriendRequest(Integer idFrom, Integer idTo) throws ValidationException, RepositoryException {
        return friendRequestService.deleteRequest(idFrom,idTo);
    }

    public List<FriendDTO> generateFriendActivity(Integer id, LocalDate startDate, LocalDate endDate) throws SQLException, RepositoryException, ValidationException {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay().plusDays(1);
        List<FriendDTO> friendList = this.getFriendDtoOfUser(id);
        return friendList.stream()
                        .filter(friendDTO-> {
                                LocalDateTime friendDate = LocalDateTime.parse(friendDTO.getDate(),DATE_TIME_FORMATTER);
                                return friendDate.isAfter(start) && friendDate.isBefore(end);
                             })
                        .collect(Collectors.toList());
    }
    public List<Message> generateFriendMessageActivity(User user1, User user2,
                                                       LocalDate startDate, LocalDate endDate) throws RepositoryException {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay().plusDays(1);
        return getConversation(user1,user2).stream()
                .filter( message -> {
                    LocalDateTime messageDate = message.getDate();
                    return messageDate.isAfter(start) && messageDate.isBefore(end);
                })
                .collect(Collectors.toList());
    }

    public void generateActivityExportPDF(User currentUser, LocalDate startDate, LocalDate endDate) throws IOException, ValidationException, SQLException, RepositoryException {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay().plusDays(1);


        PDDocument document = new PDDocument();
        PDPage first_page = new PDPage();
        PDPageContentStream contentStream = new PDPageContentStream(document,first_page);
        contentStream.setFont(new PDType1Font(TIMES_ROMAN),14);
        contentStream.setLeading(14.5f);
        contentStream.beginText();
        contentStream.showText("This is the user report for "+currentUser.getFirstName()+" "+
                currentUser.getLastName()+" between "+startDate.toString()+" "+endDate.toString());
        contentStream.newLine();
        contentStream.showText("New friends:");
        contentStream.endText();
        contentStream.close();

        PDPageContentStream contentStream2 = new PDPageContentStream(document,first_page, PDPageContentStream.AppendMode.APPEND, true);
        contentStream2.setFont(new PDType1Font(TIMES_ROMAN),12);
        contentStream2.setLeading(14.5f);
        contentStream2.beginText();
        List<FriendDTO> friends = generateFriendActivity(currentUser.getId(),startDate,endDate);
        friends.forEach(f -> {
            try {
                contentStream2.newLine();
                contentStream2.showText("     "+f.getName() +" - "+f.getDate());
            } catch (IOException e) {
                WarningBox.show("Illegal characters in usernames!");
            }
        });
        contentStream2.endText();
        contentStream2.close();

        Files.createDirectory(Path.of("C:/The Network/"));
        document.save(new File("C:/The Network/Activity.pdf"));
        document.close();
    }

    /**
     * Adds an observer to the Network service
     * @param observer Observer object
     */
    public void addObserverForNetwork(Observer observer) {
        network.addObserver(observer);
    }

    /**
     * Adds an observer to the Friend Request service
     * @param observer Observer object
     */
    public void addObserverForFriendRequests(Observer observer) { friendRequestService.addObserver(observer);}


}
