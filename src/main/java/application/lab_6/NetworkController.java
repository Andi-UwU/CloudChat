package application.lab_6;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NetworkController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
