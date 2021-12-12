package application.lab_6;

import application.domain.User;
import application.domain.FriendDTO;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;

public class MainPageController {
    private SuperService superService;
    private User user;
    @FXML
    private Label welcomeLabel;

    //========= Friends Table ===============
    @FXML
    private TableColumn<FriendDTO, Integer> friendsTableColumnId;
    @FXML
    private TableColumn<FriendDTO, String> friendsTableColumnName;
    @FXML
    private TableColumn<FriendDTO, String> friendsTableColumnFriendshipDate;
    @FXML
    private TableView<FriendDTO> friendsTableView;

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();

    //========= Delete Friend Text Field =============
    @FXML
    private TextField deleteFriendTextField;

    //========= Delete Friend Button
    @FXML
    private Button deleteFriendButton;

    public MainPageController(User user, SuperService superService) {
        this.superService=superService;
        this.user=user;
    }

    private void updateFriendsTableView(){

        try {
            friendsList.setAll(superService.getFriendDtoOfUser(user.getId()));
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        } catch (SQLException e) {
            WarningBox.show(e.getMessage());
        } catch (ValidationException e) {
            WarningBox.show(e.getMessage());
        }
        friendsTableView.setItems(friendsList);
    }
    private void initializeFriendsTableView(){

        friendsTableColumnId.setCellValueFactory(new PropertyValueFactory<FriendDTO, Integer>("id"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("name"));
        friendsTableColumnFriendshipDate.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));
        updateFriendsTableView();
        // Friends table selection
        friendsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendDTO> observable, FriendDTO oldValue, FriendDTO newValue) {
                if (newValue != null)
                    deleteFriendTextField.setText(newValue.getId().toString());
            }
        });
    }

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome " + user.getFirstName() + " " + user.getLastName() + "!");

        initializeFriendsTableView();
    }

    @FXML
    public void deleteFriendButtonAction(ActionEvent actionEvent){
        try{
            Integer friendId = Integer.parseInt(deleteFriendTextField.getText());

            superService.deleteFriendship(user.getId(), friendId);
            updateFriendsTableView();

        } catch (NumberFormatException e){
            WarningBox.show("The id must be an integer!!!");

        } catch (ValidationException | SQLException | RepositoryException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }
}
