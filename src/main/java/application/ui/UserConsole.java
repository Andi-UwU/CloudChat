package application.ui;

import application.domain.FriendRequest;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserConsole {

    private final SuperService superService;
    private BufferedReader br;
    private User currentUser;

    public UserConsole(SuperService superService){
        this.superService = superService;

        try{
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printMenu(){
        System.out.println();
    }

    private void printMainMenu(){
        System.out.println("0. Exit");
        System.out.println("1. Login");
        System.out.println("2. Create a new account");
    }

    private void showAccountInfo(){
        System.out.println("---Account");
        System.out.println("ID: " + currentUser.getId());
        System.out.println("First Name: " + currentUser.getFirstName());
        System.out.println("Last Name: " + currentUser.getLastName());
    }
    private void login() throws IOException, ValidationException, SQLException, RepositoryException {

        System.out.println("---Login");
        System.out.print("User ID: ");
        Integer id = Integer.parseInt(br.readLine());
        currentUser = superService.findUser(id);

        showAccountInfo();

        runUserConsole();
    }
    private void createAccount() throws IOException, ValidationException, RepositoryException, SQLException {
        System.out.println("---Create account");
        System.out.print("First Name: ");
        String firstName = br.readLine();
        System.out.print("Last Name: ");
        String lastName = br.readLine();

        currentUser = superService.addUser(firstName, lastName);

        showAccountInfo();

        runUserConsole();
    }

    private void printUserMenu(){
        System.out.println("0. Exit");
        System.out.println("1. Show account information");
        System.out.println("2. Show all users");
        System.out.println("3. Show all friends");
        System.out.println("4. Send a message");
        System.out.println("5. Send a reply");
        System.out.println("6. Delete a message");
        System.out.println("7. Show conversation");
        System.out.println("8. Show received friend requests");
        System.out.println("9. Show sent friend requests");
        System.out.println("10. Send friend request");
        System.out.println("11. Reply to friend request");
    }

    private void showAllUsers() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---Users:");
        superService.getAllUsers().forEach(System.out::println);
    }
    private void showAllFriends() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---Friends:");

        List<User> list = superService.friendList(currentUser);
        if (list.size() == 0)
            System.out.println("You don't have any friends =(");
        else
            list.forEach(System.out::println);
    }
    private void sendMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Send a message:");


        Integer fromId = currentUser.getId();
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
        System.out.println("Message sent.");
    }
    private void sendReply() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Send reply message:");


        Integer fromId = currentUser.getId();

        System.out.print("Message ID: ");
        Integer toId = Integer.parseInt(br.readLine());

        System.out.print("Text: ");
        String text = br.readLine();

        superService.addReply(fromId, toId, text);
        System.out.println("Reply sent.");
    }
    private void deleteMessage() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Delete Message:");
        System.out.print("Message ID: ");
        Integer id = Integer.parseInt(br.readLine());

        superService.deleteMessage(id);

        System.out.println("Message has been deleted.");
    }

    private void showConversation() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Show Conversation:");
        System.out.print("User ID: ");
        Integer id = Integer.parseInt(br.readLine());
        System.out.println("Conversation:");
        superService.getConversation(currentUser.getId(), id).forEach(System.out::println);
    }

    private void showReceivedFriendRequests() throws ValidationException, SQLException, RepositoryException {
        System.out.println("---Received friend requests:");
        superService.getAllFriendRequestsForUser(currentUser.getId()).forEach(x-> {
            System.out.println(" " + x.getUserFrom().getFirstName() +
                    " " + x.getUserFrom().getLastName() +
                    " | " + x.getStatus().toString());
        });
    }

    private void showSentFriendRequests() throws ValidationException, SQLException, RepositoryException, IOException {
        System.out.println("---Sent friend requests to:");
        superService.getAllFriendRequestsFromUser(currentUser.getId()).forEach(x-> {
            System.out.println(" " + x.getUserTo().getFirstName() +
                    " " + x.getUserFrom().getLastName() +
                    " | " + x.getStatus().toString());
        });
    }

    private void sendFriendRequest() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Send friend request to:");
        System.out.print("ID to: ");
        Integer idTo = Integer.parseInt(br.readLine());
        superService.addFriendRequest(currentUser.getId(),idTo);
    }

    private void replyToFriendRequests() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Reply to friend requests");
        System.out.print("ID from:");
        Integer idFrom = Integer.parseInt(br.readLine());
        System.out.print("Status: ");
        String status = br.readLine();

        FriendRequest request = superService.updateFriendRequest(idFrom,currentUser.getId(),status);
        System.out.println("Updated friend request from: " + request.getStatus().toString() + " to " + status);
    }

    private void deleteFriend() throws IOException, ValidationException, SQLException, RepositoryException {
        System.out.println("---Delete friendship");
        System.out.print("ID of friend:");
        Integer idDel = Integer.parseInt(br.readLine());

        superService.deleteFriendship(currentUser.getId(), idDel);
    }

    private void runUserConsole(){

        printUserMenu();
        int cmd = -1;
        while(cmd != 0){
            try{

                System.out.print(">>>");
                cmd = Integer.parseInt(br.readLine());

                switch (cmd){
                    case 0 -> {}
                    case 1 -> showAccountInfo();
                    case 2 -> showAllUsers();
                    case 3 -> showAllFriends();
                    case 4 -> sendMessage();
                    case 5 -> sendReply();
                    case 6 -> deleteMessage();
                    case 7 -> showConversation();
                    case 8 -> showReceivedFriendRequests();
                    case 9 -> showSentFriendRequests();
                    case 10-> sendFriendRequest();
                    case 11-> replyToFriendRequests();
                    case 12-> deleteFriend();
                    default -> System.out.println("Invalid command!");

                }
            } catch (IOException e) {
                System.out.println("Invalid input!");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            } catch (SQLException throwable) {
                System.out.println(throwable.getMessage());
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public void run(){



        int cmd = -1;
        while(cmd != 0){
            try{
                printMainMenu();
                System.out.print(">>>");
                cmd = Integer.parseInt(br.readLine());

                switch (cmd){
                    case 0 -> {}
                    case 1 -> login();
                    case 2 -> createAccount();
                    default -> System.out.println("Invalid command!");

                }
            } catch (IOException e) {
                System.out.println("Invalid input!");
            } catch (ValidationException e) {
                e.printStackTrace();
            } catch (SQLException throwable) {
                System.out.println(throwable.getMessage());
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
