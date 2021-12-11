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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static application.utils.DatabaseConstants.*;
import static application.utils.DatabaseConstants.PASSWORD;

public class NetworkController {
    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldPassword;

    SuperService superService;
    Stage loginStage;
    public NetworkController (Stage loginStage, SuperService superService) {
        this.loginStage=loginStage;
        this.superService=superService;
    }

    @FXML
    protected void tryLogin() {
        try {
            int userId = Integer.parseInt(textFieldUser.getText());
            User user = superService.findUser(userId);
            loginStage.hide();
            //TODO new stage probably before hide
        } catch (ValidationException | RepositoryException |  NumberFormatException ignored) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText("Invalid login information!");
            alert.showAndWait();
        }
    }
}
