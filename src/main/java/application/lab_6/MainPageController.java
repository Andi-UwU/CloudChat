package application.lab_6;

import application.domain.User;
import application.service.SuperService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainPageController {
    private SuperService superService;
    private User user;
    @FXML
    Label welcomeLabel;

    public MainPageController(User user, SuperService superService) {
        this.superService=superService;
        this.user=user;
    }

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome " + user.getFirstName() + " " + user.getLastName() + "!");
    }
}
