package application.contoller;

import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.InfoBox;
import application.utils.SceneChanger;
import application.utils.WarningBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Optional;

public class SignUpController implements Controller {
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

    @Override
    public void initializeController(SuperService superService, Optional<User> user) {
        this.superService = superService;
    }

    @FXML
    protected void createAccountAction(ActionEvent event) {
        try {
            String errors = "";

            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String username = userNameTextField.getText();
            String password = passWordTextField.getText();

            if (firstName.length()<2 || firstName.length()>25)
                errors+= "The first name must be between 2 and 25 characters!\n";
            if (lastName.length()<2 || lastName.length()>25)
                errors+= "The last name must be between 2 and 25 characters!\n";
            if (username.length()<3 || username.length()>25) {
                errors += "The username must be between 3 and 25 characters long\n";
            }
            if (password.length()<4 || password.length()>16) {
                errors += "The password must be between 4 and 16 characters long\n";
            }

            if (!errors.equals("")) {
                WarningBox.show(errors);
                return;
            }
            superService.addUser(firstName,lastName, username, password);
            InfoBox.show("Account successfully created! You can now login from the main page.");
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    protected void backButtonAction(ActionEvent event) {

        try{
            SceneChanger.changeTo(event, "network.fxml", new NetworkController(), superService);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
