package application.lab_6;

import application.domain.*;
import application.domain.validator.*;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.repository.Repository;
import application.repository.database.FriendRequestDataBaseRepository;
import application.repository.database.FriendshipDataBaseRepository;
import application.repository.database.MessageDataBaseRepository;
import application.repository.database.UserDataBaseRepository;
import application.service.FriendRequestService;
import application.service.MessageService;
import application.service.Network;
import application.service.SuperService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;

import static application.utils.DatabaseConstants.*;


public class NetworkApplication extends Application {
    private SuperService superService;

    private void initialize() throws Exception {
        try {
            // User
            Repository<Integer, User> userRepository = new UserDataBaseRepository(URL, USERNAME, PASSWORD);
            Validator<User> userValidator = new UserValidator();
            // Friendship
            Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository = new FriendshipDataBaseRepository(URL, USERNAME, PASSWORD);
            Validator<Friendship> friendshipValidator = new FriendshipValidator();
            // Network
            Network network = new Network(userRepository, userValidator, friendshipRepository, friendshipValidator);
            // Friend Request
            Validator<FriendRequest> friendRequestValidator = new FriendRequestValidator();
            Repository<Tuple<Integer,Integer>, FriendRequest> requestRepository = new FriendRequestDataBaseRepository(URL, USERNAME, PASSWORD);
            FriendRequestService friendRequestService = new FriendRequestService(requestRepository,friendRequestValidator);
            // Message
            Validator<Message> messageValidator = new MessageValidator();
            Repository<Integer, Message> messageRepository = new MessageDataBaseRepository(URL, USERNAME, PASSWORD);
            MessageService messageService = new MessageService(messageRepository, messageValidator);

            // Super Service
            this.superService= new SuperService(network, messageService,friendRequestService);
        } catch (RepositoryException | SQLException | ValidationException e) {
            System.out.println(e.getMessage());
            throw new Exception("Unable to connect to database.");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        initialize();
        NetworkController networkController = new NetworkController(superService);
        FXMLLoader fxmlLoader = new FXMLLoader(NetworkApplication.class.getResource("network.fxml"));
        fxmlLoader.setController(networkController);
        Scene loginScene = new Scene(fxmlLoader.load());
        stage.setTitle("The Network");
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}