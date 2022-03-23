package application.contoller;

import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.InfoBox;
import application.utils.WarningBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class CreateEventController {
    private SuperService superService;
    private User user;

    // Text fields

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private DatePicker datePicker;

    // Buttons

    @FXML
    private Button createButton;

    @FXML
    private Button backButton;

    // Button methods

    @FXML
    public void createButtonAction(ActionEvent actionEvent){
        try{
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            LocalDate date = datePicker.getValue();

            superService.addEvent(user, title, description, date);
            InfoBox.show("Event added!");

            backButtonAction(actionEvent);

        } catch (ValidationException e) {
            WarningBox.show(e.getMessage());
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void backButtonAction(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            MainPageController mainPageController = new MainPageController();
            fxmlLoader.setLocation(getClass().getResource("mainpage.fxml"));
            fxmlLoader.setController(mainPageController);
            Scene mainScene = new Scene(fxmlLoader.load());
            Stage mainStage = new Stage();
            mainStage.setTitle("Cloud Chat");
            mainStage.setScene(mainScene);
            mainStage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    public void setService(SuperService superService) {
        this.superService = superService;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
