package application.lab_6;

import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.InfoBox;
import application.utils.WarningBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SignUpController {
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField passWordTextField;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button backButton;

    private SuperService superService;

    public SignUpController(SuperService superService) {
        this.superService=superService;
    }

    @FXML
    protected void createAccountAction(ActionEvent event) {
        try {
            superService.addUser(firstNameTextField.getText(),lastNameTextField.getText(),
                                userNameTextField.getText(),passWordTextField.getText());
            InfoBox.show("Account successfully created! You can now login from the main page.");
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    protected void backButtonAction(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
