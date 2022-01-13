package application.service;

import application.domain.*;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;
import application.utils.ExporterPDF;
import application.utils.observer.Observer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static application.utils.Constants.DATE_TIME_FORMATTER;
import static java.time.temporal.ChronoUnit.DAYS;

public class SuperService {
    private final Network network;

    private final MessageService messageService;
    private final FriendRequestService friendRequestService;
    private final EventService eventService;

    public SuperService(Network network, MessageService messageService, FriendRequestService friendRequestService, EventService eventService) {
        this.network = network;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
        this.eventService = eventService;
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
                    return new UserDTO(id, name);
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
    public Friendship updateFriendship(Integer leftId, Integer rightId, LocalDateTime date) throws ValidationException, RepositoryException {

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

    public Message deleteMessage(Integer messageId) throws RepositoryException {
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

        List<FriendDTO> friendDTOList = generateFriendActivity(currentUser.getId(),startDate,endDate);
        List<FriendDTO> fullFriendList = getFriendDtoOfUser(currentUser.getId());
        List<List<Message>> messageList = new ArrayList<>();
        for (FriendDTO friend : fullFriendList) {
            messageList.add(generateFriendMessageActivity(currentUser,findUser(friend.getId()),startDate,endDate)
                             .stream()
                             .filter(m -> m.getFrom()!=currentUser)
                             .collect(Collectors.toList()));
        }

        ExporterPDF exporter = new ExporterPDF();   // create a new ExporterPDF instance to create a PDF file
        exporter.exportActivityToPDF(currentUser, startDate, endDate,
                friendDTOList, messageList
        );
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


    // ===================== EVENTS ==========================

    public List<Event> getAllEvents() throws RepositoryException {
        return eventService.getAll();
    }

    public Event findEvent(Integer eventId) throws RepositoryException {
        return eventService.find(eventId);
    }

    public Event addEvent(User author, String title, String description, LocalDate eventDate) throws ValidationException, RepositoryException {
        return eventService.add(author, title, description, eventDate);
    }

    public Event deleteEvent(Integer eventId) throws RepositoryException {
        return eventService.delete(eventId);
    }

    public Event updateEvent(Event oldEvent, String newTitle, String newDescription) throws RepositoryException, ValidationException {
        return eventService.update(oldEvent, newTitle, newDescription);
    }

    public Integer getNoOfEvents() throws SQLException {
        return eventService.size();
    }

    public Event addSubscriber(Event event, User subscriber) throws RepositoryException {
        return eventService.addSubscriber(event, subscriber);
    }

    public Event removeSubscriber(Event event, User subscriber) throws RepositoryException{
        return eventService.removeSubscriber(event, subscriber);
    }

    public List<Event> getSubscribedEventsForUser(User user) throws RepositoryException {
        return eventService.getAll()
                .stream()
                .filter(event -> event.getSubscribers().contains(user))
                .sorted((event1, event2) ->
                {
                    if (event1.getEventDate().isBefore(event2.getEventDate()))
                        return -1;
                    return 1;
                })
                .collect(Collectors.toList());

    }

    public List<Event> getEventsForUser(User user) throws RepositoryException, SQLException {
        List<Event> eventList = eventService.getAll();
        List<User> friendsList = friendList(user);

        return eventList
                .stream()
                .filter(event -> event.getEventDate().isAfter(LocalDate.now()) || event.getEventDate().equals(LocalDate.now()))
                .filter(event -> {
                    if (event.getAuthor().equals(user)) // if the author is the user
                        return true;
                    if (friendsList.contains(event.getAuthor())) // if the author is one of the user's friend
                        return true;
                    List<User> intersectionList = friendsList  // intersection of friends of the user and subscribers of the event
                            .stream()
                            .distinct()
                            .filter(event.getSubscribers()::contains)
                            .collect(Collectors.toList());
                    if (intersectionList.size() > 0) // if there is one of the user's friend subscribed to this event
                        return true;

                    return false;
                })
                .sorted((event1, event2) ->
                {
                    if (event1.getEventDate().isBefore(event2.getEventDate()))
                        return -1;
                    return 1;
                })
                .collect(Collectors.toList());
    }

    public Integer getNumberOfSoonEventsForUser(User user) throws RepositoryException {
        long notificationsNumber = eventService.getAll()
                .stream()
                .filter(event -> event.getSubscribers().contains(user))
                .filter(event -> DAYS.between(LocalDate.now(), event.getEventDate()) < 2)
                .filter(event -> event.getEventDate().isAfter(LocalDate.now()) || event.getEventDate().equals(LocalDate.now()))
                .sorted((event1, event2) ->
                {
                    if (event1.getEventDate().isBefore(event2.getEventDate()))
                        return -1;
                    return 1;
                })
                .count();
        return (int) notificationsNumber;
    }
}
