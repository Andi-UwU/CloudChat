package application.ui;

import application.domain.FriendRequest;
import application.domain.Friendship;
import application.domain.Message;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminConsole {
    private final SuperService superService;
    private BufferedReader br;

    public AdminConsole(SuperService superService){
        this.superService = superService;

        try{
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMenu(){

        System.out.println(" 0. Exit");
        // User menu
        System.out.println(" 1. Show users");
        System.out.println(" 2. Add user");
        System.out.println(" 3. Delete user");
        System.out.println(" 4. Update user");
        System.out.println(" 5. Find user");
        // Friendship menu
        System.out.println(" 6. Show friendships");
        System.out.println(" 7. Add friendship");
        System.out.println(" 8. Delete friendship");
        System.out.println(" 9. Update friendship");
        System.out.println("10. Find friendship");
        // Network
        System.out.println("11. Get communities number");
        System.out.println("12. Show friends of a user");
        System.out.println("13. Show friends of a user from a specified month");
        //Message
        System.out.println("20. Show all messages");
        System.out.println("21. Add message");
        System.out.println("22. Add reply message");
        System.out.println("23. Delete message");
        System.out.println("24. Update message");
        System.out.println("25. Find message");
        System.out.println("26. Show conversation between 2 users");
        // Friend Requests
        System.out.println("30. ADMIN - See all friend requests");
        System.out.println("31. Show received friend requests");
        System.out.println("32. Show sent friend requests");
        System.out.println("33. Send new friend request");
        System.out.println("34. Reply to friend request");
        System.out.println("35. Delete friend request");
    }

    // ====================== USERS ============================
    private void showUsers() throws SQLException, RepositoryException, ValidationException {
        System.out.println("---Users:");
        for(User user : superService.getAllUsers()){
            System.out.println(user);
            System.out.println("Friends: " + superService.friendList(user));
        }
    }

    private void addUser() throws ValidationException, IOException, RepositoryException, SQLException {
        System.out.println("---Add user:");
        System.out.print("First Name: ");
        String firstName = br.readLine();
        System.out.print("Last Name: ");
        String lastName = br.readLine();

        superService.addUser(firstName, lastName);
        System.out.println("User has been added.\n");
    }
    private void deleteUser() throws IOException, SQLException, RepositoryException, ValidationException {
        System.out.println("---Delete user:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());
        superService.deleteUser(id);

        System.out.println("User has been deleted.");
    }

    private void updateUser() throws IOException, ValidationException, RepositoryException, SQLException {
        System.out.println("---Update user:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());
        System.out.print("First Name: ");
        String firstName = br.readLine();
        System.out.print("Last Name: ");
        String lastName = br.readLine();

        superService.updateUser(id, firstName, lastName);

        System.out.println("User has been updated.");

    }

    private void findUser() throws IOException, SQLException, RepositoryException, ValidationException {
        System.out.println("---Find user:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());

        User user = superService.findUser(id);

        System.out.println(user);
        System.out.println("Friends: " + superService.friendList(user));

    }
    // ====================== FRIENDSHIPS ============================
    private void showFriendships() throws SQLException, ValidationException, RepositoryException {
        System.out.println("---Friendships:");
        for(Friendship f : superService.getAllFriendship()){
            System.out.println(f);
        }
    }

    private void addFriendship() throws IOException, ValidationException, RepositoryException, SQLException {
        System.out.println("---Add friendship:");
        System.out.print("ID1: ");
        Integer id1 = Integer.parseInt(br.readLine());
        System.out.print("ID2: ");
        Integer id2 = Integer.parseInt(br.readLine());
        superService.addFriendship(id1, id2);

        System.out.println("Friendship has been added.\n");
    }

    private void deleteFriendship() throws IOException, RepositoryException, SQLException, ValidationException {
        System.out.println("---Delete friendship:");
        System.out.print("ID1: ");
        Integer id1 = Integer.parseInt(br.readLine());
        System.out.print("ID2: ");
        Integer id2 = Integer.parseInt(br.readLine());

        superService.deleteFriendship(id1, id2);

        System.out.println("Friendship has been deleted.");
    }

    private void updateFriendship() throws IOException, ValidationException, RepositoryException, SQLException {
        System.out.println("---Update friendship:");
        System.out.print("ID1: ");
        Integer id1 = Integer.parseInt(br.readLine());
        System.out.print("ID2: ");
        Integer id2 = Integer.parseInt(br.readLine());
        System.out.print("Date: ");
        LocalDateTime date = LocalDateTime.parse(br.readLine());

        superService.updateFriendship(id1, id2, date);

        System.out.println("Friendship has been updated.");
    }

    private void findFriendship() throws IOException, SQLException, RepositoryException, ValidationException {
        System.out.println("---Find friendship:");
        System.out.print("ID1: ");
        Integer id1 = Integer.parseInt(br.readLine());
        System.out.print("ID2: ");
        Integer id2 = Integer.parseInt(br.readLine());

        Friendship friendship = superService.findFriendship(id1, id2);
        System.out.println(friendship);
    }

    // ====================== NETWORK ============================

    private void getCommunitiesNumber() throws SQLException, ValidationException, RepositoryException {

        System.out.println("---Communities number: " + superService.getCommunitiesNumber());
    }

    private void showFriendsOfUser() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Friends of user:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());

        System.out.println("Friends:");
        superService.getFriendshipsOfUser(id).forEach(System.out::println);
    }

    private void showFriendsOfUserFromMonth() throws IOException,ValidationException,SQLException,RepositoryException {
        System.out.println("---Friends of user:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());
        System.out.print("Value of month: ");
        Integer month = Integer.parseInt(br.readLine());

        System.out.println("Friends:");
        superService.getFriendshipsOfUserFromMonth(id,month).forEach(System.out::println);
    }
    // ====================== MESSAGES ============================

    private void showMessages() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---Messages:");
        for(Message m : superService.getAllMessages()){
            System.out.println(m);
        }
    }
    private void addMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Add message: (Write 0 when done with receivers)");
        System.out.print("From ID: ");
        Integer fromId = Integer.parseInt(br.readLine());
        List<Integer> toIds = new ArrayList<>();

        System.out.print("Receiver ID: ");
        int toId = Integer.parseInt(br.readLine());
        while (toId != 0) {
            toIds.add(toId);
            System.out.print("Receiver ID: ");
            toId = Integer.parseInt(br.readLine());
        }

        System.out.print("Text: ");
        String text = br.readLine();

        superService.addMessage(fromId, toIds, text);
        System.out.println("Message added.");
    }
    private void addReply() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Add reply message:");

        System.out.print("From ID: ");
        Integer fromId = Integer.parseInt(br.readLine());

        System.out.print("Message ID: ");
        Integer toId = Integer.parseInt(br.readLine());

        System.out.print("Text: ");
        String text = br.readLine();

        superService.addReply(fromId, toId, text);
        System.out.println("Reply added.");
    }
    private void deleteMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Delete Message:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());

        superService.deleteMessage(id);

        System.out.println("Message has been deleted.");

    }
    private void updateMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Delete Message:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());
        System.out.print("New Text: ");
        String newText = br.readLine();

        superService.updateMessage(id, newText);

        System.out.println("Message has been updated.");
    }
    private void findMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Find Message:");
        System.out.print("ID: ");
        Integer id = Integer.parseInt(br.readLine());

        System.out.println(superService.findMessage(id));
    }

    private void showConversation() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Show Conversation:");
        System.out.print("ID 1: ");
        Integer id1 = Integer.parseInt(br.readLine());
        System.out.print("ID 2: ");
        Integer id2 = Integer.parseInt(br.readLine());
        System.out.println("Conversation:");
        superService.getConversation(id1, id2).forEach(System.out::println);
    }

    private void ADMINseeAllFriendRequests() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---All friend requests:");
        superService.getAllFriendRequests().forEach(System.out::println);
    }

    private void showReceivedFriendRequests() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---Received friend requests:");
        Integer userId=5;
        superService.getAllFriendRequestsForUser(userId).forEach(x-> {
            System.out.println(" " + x.getUserFrom().getFirstName() +
                               " " + x.getUserFrom().getLastName() +
                               " | " + x.getStatus().toString());
        });
    }

    private void showSentFriendRequests() throws ValidationException, SQLException, RepositoryException, IOException {
        System.out.println("---Sent friend requests to:");
        System.out.print("Your user ID: ");
        Integer userId = Integer.parseInt(br.readLine());
        superService.getAllFriendRequestsFromUser(userId).forEach(x-> {
            System.out.println(" " + x.getUserTo().getFirstName() +
                    " " + x.getUserFrom().getLastName() +
                    " | " + x.getStatus().toString());
        });
    }

    private void sendFriendRequest() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Send friend request to:");
        System.out.print("ID from: ");
        Integer idFrom = Integer.parseInt(br.readLine());
        System.out.print("ID to: ");
        Integer idTo = Integer.parseInt(br.readLine());
        superService.addFriendRequest(idFrom,idTo);
    }

    private void replyToFriendRequests() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Reply to friend requests");
        System.out.print("ID from:");
        Integer idFrom = Integer.parseInt(br.readLine());
        System.out.print("ID to: ");
        Integer idTo = Integer.parseInt(br.readLine());
        System.out.print("Status: ");
        String status = br.readLine();

        FriendRequest request = superService.updateFriendRequest(idFrom,idTo,status);
        System.out.println("Updated friend request from: " + request.getStatus().toString() + " to " + status);
    }

    private void deleteFriendRequest() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Delete friend request");
        System.out.print("ID from:");
        Integer idFrom = Integer.parseInt(br.readLine());
        System.out.print("ID to: ");
        Integer idTo = Integer.parseInt(br.readLine());

        FriendRequest request = superService.deleteFriendRequest(idFrom,idTo);
        System.out.println( "[DELETED] : " + request);
    }


    public void run(){
        printMenu();

        int cmd = -1;
        while (cmd != 0){
            try{
                System.out.print(">>>");

                String line = br.readLine();
                cmd = Integer.parseInt(line);

                switch (cmd){
                    // EXIT
                    case 0 -> {}
                    // USERS
                    case 1 -> showUsers();
                    case 2 -> addUser();
                    case 3 -> deleteUser();
                    case 4 -> updateUser();
                    case 5 -> findUser();
                    // FRIENDSHIPS
                    case 6 -> showFriendships();
                    case 7 -> addFriendship();
                    case 8 -> deleteFriendship();
                    case 9 -> updateFriendship();
                    case 10 -> findFriendship();
                    // NETWORK
                    case 11 -> getCommunitiesNumber();
                    case 12 -> showFriendsOfUser();
                    case 13 -> showFriendsOfUserFromMonth();
                    // MESSAGES
                    case 20 -> showMessages();
                    case 21 -> addMessage();
                    case 22 -> addReply();
                    case 23 -> deleteMessage();
                    case 24 -> updateMessage();
                    case 25 -> findMessage();
                    case 26 -> showConversation();
                    case 30 -> ADMINseeAllFriendRequests();
                    case 31 -> showReceivedFriendRequests();
                    case 32 -> showSentFriendRequests();
                    case 33 -> sendFriendRequest();
                    case 34 -> replyToFriendRequests();
                    case 35 -> deleteFriendRequest();
                    default -> System.out.println("Invalid command!");
                }

            } catch (NumberFormatException e){
                System.out.println("Invalid command!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
