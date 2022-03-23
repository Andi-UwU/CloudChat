package application.service;

import application.domain.*;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;
import application.utils.ExporterPDF;
import application.utils.observer.Observer;

import java.io.IOException;
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

    // ===================== NETWORK ==========================
    /**
     * gets the friend list of the given user
     * @param user from which is extracted the friend list
     * @return the friend list of the user as Iterable
     */
    public List<User> friendList(User user) throws RepositoryException {
        return network.friendList(user);
    }

    /**
     * gets a string list of all the friendships the user has
     * @param userId Integer
     * @return List(String)
     * @throws RepositoryException if a user doesn't exist
     */
    public List<String> getFriendshipsOfUser(Integer userId) throws RepositoryException {
        return network.getFriendshipsOfUser(userId);
    }

    /**
     * gets a string list of all the friendships a user has made in a specific month
     * @param userId Integer
     * @param month Integer
     * @return List(String)
     */
    public List<String> getFriendshipsOfUserFromMonth(Integer userId, Integer month) throws ValidationException, RepositoryException {
        return network.getFriendshipsOfUserFromMonth(userId, month);
    }

    /**
     * Gets friend data transfer objects of given user
     * @param id Integer
     * @return List(FriendDTO)
     * @throws RepositoryException if there is no user with given ID
     */
    public List<FriendDTO> getFriendDtoOfUser(Integer id) throws RepositoryException {
        return network.getFriendDtoOfUser(id);
    }

    /**
     * Gets list of message receivers
     * @param message Message
     * @return List(UserDTO)
     */
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

    /**
     * Gets users that are not friends of the user
     * @param id Integer
     * @return List(User)
     * @throws RepositoryException if the user doesn't exist
     */
    public List<User> getNonFriendOfUser(Integer id) throws RepositoryException {
        List<User> friendsOfUser = friendList(network.findUser(id));
        return getAllUsers()
                .stream()
                .filter(user -> !(friendsOfUser.contains(user)) && !user.getId().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of added friends for a user
     * @param id Integer
     * @return List(AddFriendDTO)
     * @throws RepositoryException if the user with the given id doesn't exist
     */
    public List<AddFriendDTO> getAddFriendDtoOfUser(Integer id) throws RepositoryException {

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

    /**
     * Gets list of users that are not friends filtered by name
     * @param id Integer
     * @param name String
     * @return List(AddFriendDTO)
     * @throws RepositoryException if the user doesn't exist
     */
    public List<AddFriendDTO> getAddFriendDtoOfUserByName(Integer id, String name) throws RepositoryException {
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
     * @throws ValidationException if the user is invalid
     * @throws RepositoryException if the user doesn't exist
     */
    public User deleteUser(Integer id) throws RepositoryException, ValidationException {

        friendRequestService.deleteRequestsOfUser(id);  // delete all friend requests
        User deleted = network.findUser(id);            // delete user
        messageService.deleteMessagesOfUser(deleted);   // delete all messages related to user
        return network.deleteUser(id);
    }

    /**
     * Updates a user
     * @param firstName String
     * @param lastName String
     * @param userName String
     * @return User
     * @throws ValidationException if the attributes are invalid
     * @throws RepositoryException if the username already exists
     */
    public User updateUser(String firstName, String lastName, String userName) throws ValidationException, RepositoryException {
        return network.updateUser(new User(firstName,lastName,userName));
    }

    /**
     * gets all users of the network
     * @return all users as Iterable
     * @throws RepositoryException if the repository is empty
     */
    public List<User> getAllUsers() throws RepositoryException {
        return network.getAllUsers();
    }

    /**
     * Finds a user with the given id
     * @param id Integer
     * @return User
     */
    public User findUser(Integer id) throws RepositoryException {
        return network.findUser(id);
    }

    /**
     * Logs a user in and returns their ID if it is successful
     * @param username String
     * @param password String
     * @return int
     * @throws RepositoryException if the password is invalid
     */
    public int loginUser(String username, String password) throws RepositoryException {
        return network.loginUser(username, password);
    }

    //=================== FRIENDSHIPS =======================


    /**
     * Adds a friendship
     * @param leftId id of a user
     * @param rightId id of another user
     * @throws ValidationException if the friendship is not valid
     * @throws RepositoryException if the friendship already exists
     */
    public void addFriendship(Integer leftId, Integer rightId) throws ValidationException, RepositoryException {
        network.addFriendship(leftId, rightId);
    }

    /**
     * Deletes a friendship
     * @param leftId Integer
     * @param rightId Integer
     * @return the friendship that has been deleted
     * @throws RepositoryException if the friendship isn't found
     */
    public Friendship deleteFriendship(Integer leftId, Integer rightId) throws RepositoryException {
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
     * Updates a friendship
     * @param leftId Integer
     * @param rightId Integer
     * @param date LocalDateTime
     * @return  the friendship that has been updated
     * @return null, if there is no friendship with (leftId, rightId) as id
     * @throws ValidationException if the date is invalid
     * @throws  RepositoryException if the friendship doesn't exist
     */
    public Friendship updateFriendship(Integer leftId, Integer rightId, LocalDateTime date) throws ValidationException, RepositoryException {
        return network.updateFriendship(leftId, rightId, date);
    }

    /**
     * Finds a friendship
     * @param leftId Integer
     * @param rightId Integer
     * @return  Friendship
     * @throws RepositoryException if the friendship doesn't exist
     */
    public Friendship findFriendship(Integer leftId, Integer rightId) throws RepositoryException {
        return network.findFriendship(leftId, rightId);
    }

    /**
     * Gets all friendships from the repository
     * @return List(Friendship)
     */
    public List<Friendship> getAllFriendship() throws RepositoryException {
        return network.getAllFriendship();
    }

    // ===================== MESSAGE ==========================


    /**
     * Gets all messages
     * @return Lits(Message)
     * @throws RepositoryException
     */
    public List<Message> getAllMessages() throws RepositoryException {
        return messageService.getAll();
    }

    /**
     * Finds a message
     * @param id Integer
     * @return Message
     * @throws RepositoryException if the message doesn't exist
     */
    public Message findMessage(Integer id) throws RepositoryException {
        return messageService.find(id);
    }

    /**
     * Adds a message
     * @param fromId Integer
     * @param toIds List(Integer)
     * @param text String
     * @return Message
     * @throws ValidationException if the message attributes are invalid
     * @throws RepositoryException if the message can't be added
     */
    public Message addMessage(Integer fromId, List<Integer> toIds, String text) throws ValidationException, RepositoryException {
        User from = network.findUser(fromId);
        List<User> to = new ArrayList<>();
        for(Integer id : toIds){
            to.add(network.findUser(id));
        }

        return messageService.addMessage(from, to, text);
    }

    /**
     * Adds a reply to a message
     * @param fromId Integer
     * @param replyToId Integer
     * @param text String
     * @throws ValidationException if the message attributes are invalid
     * @throws RepositoryException if the message replying to is not found
     */
    public void addReply(Integer fromId, Integer replyToId, String text) throws ValidationException, RepositoryException {
        User from = network.findUser(fromId);
        Message replyTo = messageService.find(replyToId);
        messageService.addReply(from, text, replyTo);
    }

    /**
     * Adds a reply to all receivers of previous message
     * @param fromId Integer
     * @param replyToId Integer
     * @param text String
     * @throws RepositoryException if the message replying to, doesn't exist
     * @throws ValidationException if the message attributes are invalid
     */
    public void addReplyToAll(Integer fromId, Integer replyToId, String text) throws RepositoryException, ValidationException {
        User from = network.findUser(fromId);
        Message replyTo = messageService.find(replyToId);
        messageService.addReplyToAll(from,text,replyTo);
    }

    /**
     * Updates a message
     * @param messageId Integer
     * @param newText String
     * @return Message
     * @throws ValidationException if the message attributes are invalid
     * @throws RepositoryException if the message doesn't exist
     */
    public Message updateMessage(Integer messageId, String newText) throws ValidationException, RepositoryException {
        Message message = messageService.find(messageId);
        return messageService.update(messageId, message.getTo(), newText);
    }

    /**
     * Deletes a message
     * @param messageId Integer
     * @return Message
     * @throws RepositoryException if the message doesn't exist
     */
    public Message deleteMessage(Integer messageId) throws RepositoryException {
        return messageService.delete(messageId);
    }

    /**
     * Deletes a message in a conversation between users
     * @param user User
     * @param messageId Integer
     * @return Message
     * @throws RepositoryException if the message doesn't exist
     * @throws ServiceException if the message doesn't belong to the user
     */
    public Message userDeleteMessage(User user, Integer messageId) throws RepositoryException, ServiceException {
        Message message = findMessage(messageId);
        if (!message.getFrom().equals(user))
            throw new ServiceException("This message does not belong to you!\n");

        deleteMessage(messageId);

        return message;
    }

    /**
     * Gets size of message repository
     * @return Integer
     * @throws RepositoryException
     */
    public Integer getMessagesSize() throws RepositoryException {
        return messageService.size();
    }

    /**
     * Gets a conversation between two users
     * @param user1 User
     * @param user2 User
     * @return List(Message)
     * @throws RepositoryException if the users don't exist
     */
    public List<Message> getConversation(User user1, User user2) throws RepositoryException {

        return messageService.getConversation(user1, user2);
    }

    /**
     * Gets a page from a conversation between two users
     * @param user1 User
     * @param user2 User
     * @param page Integer
     * @return List(Message)
     * @throws RepositoryException if the users are not found
     */
    public List<Message> getConversationPage(User user1, User user2, Integer page) throws RepositoryException {
        return messageService.getConversationPage(user1, user2, page);
    }

    /**
     * Gets the number of pages a conversation between two users has
     * @param user1 User
     * @param user2 User
     * @return Integer
     * @throws RepositoryException if the users are not found
     */
    public Integer getNumberOfConversationPages(User user1, User user2) throws RepositoryException {
        return messageService.getNumberOfConversationPages(user1, user2);
    }

    // ===================== FRIEND REQUEST ==========================

    /**
     * Gets all friend requests for a user
     * @param id Integer
     * @return List(FriendRequestDTO)
     * @throws RepositoryException if the user isn't found
     */
    public List<FriendRequestDTO> getAllFriendRequestsDtoForUser(Integer id ) throws RepositoryException {
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

    /**
     * Gets all friend requests
     * @return List(FriendRequest)
     * @throws RepositoryException if there are no friend requests
     */
    public List<FriendRequest> getAllFriendRequests() throws RepositoryException {
        return friendRequestService.getAll();
    }

    /**
     * Gets all friend requests for a user
     * @param id Integer
     * @return List(FriendRequest)
     * @throws RepositoryException if the user doesn't exist
     */
    public List<FriendRequest> getAllFriendRequestsForUser(Integer id) throws RepositoryException {
        findUser(id);
        return friendRequestService.getAllToUser(id);
    }

    /**
     * Gets all friend requests from a user
     * @param id Integer
     * @return List(FriendRequests)
     * @throws RepositoryException if the user doesn't exist
     */
    public List <FriendRequest> getAllFriendRequestsFromUser(Integer id) throws RepositoryException {
        findUser(id);
        return friendRequestService.getAllFromUser(id);
    }

    /**
     * Adds friend request
     * @param idFrom Integer
     * @param idTo Integer
     * @throws ValidationException if the attributes are invalid
     * @throws RepositoryException if the friendship already exists
     */
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

    /**
     * Updates a friend request
     * @param idFrom Integer
     * @param idTo Integer
     * @param status String
     * @return FriendRequest
     * @throws ValidationException if the attributes are invalid
     * @throws RepositoryException if the friend request doesn't exist
     */
    public FriendRequest updateFriendRequest(Integer idFrom,Integer idTo, String status) throws ValidationException, RepositoryException {
        FriendRequest request = friendRequestService.updateRequest(idFrom,idTo,status);
        if (status.equals("ACCEPTED")) {
            addFriendship(idFrom,idTo);
        }
        return request;
    }

    /**
     * Deletes a friend request
     * @param idFrom Integer
     * @param idTo Integer
     * @return FriendRequest
     * @throws RepositoryException if the friend request doesn't exist
     */
    public FriendRequest deleteFriendRequest(Integer idFrom, Integer idTo) throws RepositoryException {
        return friendRequestService.deleteRequest(idFrom,idTo);
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

    /**
     * Returns a list of all events
     * @return List(Event)
     * @throws RepositoryException if there are no events
     */
    public List<Event> getAllEvents() throws RepositoryException {
        return eventService.getAll();
    }

    /**
     * Finds an event
     * @param eventId Integer
     * @return Event
     * @throws RepositoryException if event doesn't exist
     */
    public Event findEvent(Integer eventId) throws RepositoryException {
        return eventService.find(eventId);
    }

    /**
     * Adds an event
     * @param author User
     * @param title String
     * @param description String
     * @param eventDate LocalDate
     * @return Event
     * @throws ValidationException if the event attributes are invalid
     * @throws RepositoryException if the event already exists
     */
    public Event addEvent(User author, String title, String description, LocalDate eventDate) throws ValidationException, RepositoryException {
        return eventService.add(author, title, description, eventDate);
    }

    /**
     * Deletes an event
     * @param eventId Integer
     * @return Event
     * @throws RepositoryException if the event cannot be found
     */
    public Event deleteEvent(Integer eventId) throws RepositoryException {
        return eventService.delete(eventId);
    }

    /**
     * Updates an event
     * @param oldEvent Event
     * @param newTitle String
     * @param newDescription String
     * @return Event
     * @throws RepositoryException if the event cannot be found
     * @throws ValidationException if the attributes are invalid
     */
    public Event updateEvent(Event oldEvent, String newTitle, String newDescription) throws RepositoryException, ValidationException {
        return eventService.update(oldEvent, newTitle, newDescription);
    }

    /**
     * Gets number of events
     * @return Integer
     * @throws RepositoryException
     */
    public Integer getNoOfEvents() throws RepositoryException {
        return eventService.size();
    }

    /**
     * Adds a subscriber to an event
     * @param event Event
     * @param subscriber User
     * @return Event
     * @throws RepositoryException if the event doesn't exist
     */
    public Event addSubscriber(Event event, User subscriber) throws RepositoryException {
        return eventService.addSubscriber(event, subscriber);
    }

    /**
     * Removes a subscriber from an event
     * @param event Event
     * @param subscriber User
     * @return Event
     * @throws RepositoryException if the event doesn't exist
     */
    public Event removeSubscriber(Event event, User subscriber) throws RepositoryException{
        return eventService.removeSubscriber(event, subscriber);
    }

    /**
     * Gets a list of events a user has subscribed to
     * @param user User
     * @return List(Event)
     * @throws RepositoryException if the user doesn't exist
     */
    public List<Event> getSubscribedEventsForUser(User user) throws RepositoryException {
        return eventService.getAll()
                .stream()
                .filter(event -> event.getEventDate().isAfter(LocalDate.now()) || event.getEventDate().equals(LocalDate.now()))
                .filter(event -> event.getSubscribers().contains(user))
                .sorted((event1, event2) ->
                {
                    if (event1.getEventDate().isBefore(event2.getEventDate()))
                        return -1;
                    return 1;
                })
                .collect(Collectors.toList());

    }

    /**
     * Gets a list of events for a user
     * @param user User
     * @return List(Event)
     * @throws RepositoryException if the user doesn't exist
     */
    public List<Event> getEventsForUser(User user) throws RepositoryException {
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

    /**
     * Gets number of events that will happen soon for a user
     * @param user User
     * @return Integer
     * @throws RepositoryException if the user doesn't exist
     */
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
