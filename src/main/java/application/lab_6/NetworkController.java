package application.lab_6;

import application.domain.*;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NetworkController {
    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Label welcomeLabel;

    SuperService superService;
    public NetworkController (SuperService superService) {
        this.superService=superService;
    }

    @FXML
    protected void tryLogin(ActionEvent event) {
        try {
            int userId = Integer.parseInt(textFieldUser.getText());
            User user = superService.findUser(userId);
            FXMLLoader fxmlLoader = new FXMLLoader();
            MainPageController mainPageController = new MainPageController(user,superService);
            fxmlLoader.setLocation(getClass().getResource("mainpage.fxml"));
            fxmlLoader.setController(mainPageController);
            Scene mainScene = new Scene(fxmlLoader.load());
            Stage mainStage = new Stage();
            mainStage.setTitle("The Network");
            mainStage.setScene(mainScene);
            mainStage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

        } catch (ValidationException | RepositoryException | NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }
}
