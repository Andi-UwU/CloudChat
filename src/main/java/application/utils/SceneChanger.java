package application.utils;

import application.cloud_chat.NetworkApplication;
import application.contoller.Controller;
import application.contoller.SignUpController;
import application.domain.User;
import application.service.SuperService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SceneChanger {

    /***
     * Change the scene of the event with the new scene from scenePath with the controller
     * The controller of the scene will be initialized with the superService and user
     * The title of the new scene will be changed with the windowTitle
     * @param event
     * @param scenePath
     * @param controller
     * @param superService
     * @param user
     * @param windowTitle
     * @throws IOException
     */
    public static <T extends Controller> void changeTo(Event event, String scenePath, Controller controller, SuperService superService, Optional<User> user, String windowTitle) throws IOException {



        FXMLLoader fxmlLoader = new FXMLLoader(NetworkApplication.class.getResource(scenePath));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        controller.initializeController(superService, user);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setTitle(windowTitle);
        stage.setScene(scene);
    }
    // no titleWindow
    public static void changeTo(Event event, String scenePath, Controller controller, SuperService superService, Optional<User> user) throws IOException {
        changeTo(event, scenePath, controller, superService, user, "CloudChat");
    }
    // no user
    public static void changeTo(Event event, String scenePath, Controller controller, SuperService superService, String windowTitle) throws IOException {
        changeTo(event, scenePath, controller, superService, Optional.empty(), windowTitle);
    }
    // no titleWindow and no user
    public static void changeTo(Event event, String scenePath, Controller controller, SuperService superService) throws IOException {
        changeTo(event, scenePath, controller, superService, Optional.empty(), "CloudChat");
    }
}
