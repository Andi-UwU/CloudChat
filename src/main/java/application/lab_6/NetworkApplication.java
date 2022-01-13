package application.lab_6;

import application.domain.*;
import application.domain.validator.*;
import application.repository.Repository;
import application.repository.database.*;
import application.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static application.utils.DatabaseConstants.*;


public class NetworkApplication extends Application {
    private SuperService superService;

    private void initialize() {
        // User
        UserDataBaseRepository userRepository = new UserDataBaseRepository(URL, USERNAME, PASSWORD);
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
        MessageDataBaseRepository messageRepository = new MessageDataBaseRepository(URL, USERNAME, PASSWORD);
        MessageService messageService = new MessageService(messageRepository, messageValidator);

        // Event
        Validator<Event> eventValidator = new EventValidator();
        EventDataBaseRepository eventDataBaseRepository = new EventDataBaseRepository(URL, USERNAME, PASSWORD);
        EventService eventService = new EventService(eventDataBaseRepository, eventValidator);


        // Super Service
        this.superService= new SuperService(network, messageService, friendRequestService, eventService);

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