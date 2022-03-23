package application.contoller;

import application.cloud_chat.NetworkApplication;
import application.domain.*;
import application.exceptions.RepositoryException;
import application.service.SuperService;
import application.utils.SceneChanger;
import application.utils.WarningBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class NetworkController implements Controller {
    @FXML
    private TextField textFieldUser;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label signUpButton;

    SuperService superService;
    private Stage stage;
    private Scene scene;


    @FXML
    protected void tryLogin(ActionEvent event) {
        try {

            String username = textFieldUser.getText();
            String password = passwordField.getText();

            Integer id = superService.loginUser(username, password);
            if (id > 0) {
                User user = superService.findUser(id);
                // create user's main page
                SceneChanger.changeTo(event, "mainpage.fxml", new MainPageController(), superService, Optional.of(user));
            } else
                WarningBox.show("Invalid login information!");

        } catch (RepositoryException | NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    protected void signUpView(MouseEvent event) {
        try {
            SceneChanger.changeTo(event, "signUp.fxml", new SignUpController(), superService);

        } catch (IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @Override
    public void initializeController(SuperService superService, Optional<User> user) {
        this.superService = superService;
    }
}
