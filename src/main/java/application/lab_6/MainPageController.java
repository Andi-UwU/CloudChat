package application.lab_6;

import application.domain.User;
import application.domain.FriendDTO;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import application.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.sql.SQLException;


public class MainPageController implements Observer {
    private SuperService superService;
    private User user;

    private Scene friendRequestWindow = null;

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

    //========= Delete Friend Button
    @FXML
    private Button deleteFriendButton;

    @FXML
    private Button addFriendButton;

    //========= Friend Requests Button
    @FXML
    private Button friendRequestsButton;


    //========= Back to login button
    @FXML
    private Button backToLoginButton;

    public MainPageController(User user, SuperService superService) {
        this.superService=superService;
        superService.addObserverForNetwork(this);
        this.user=user;
    }

    private void updateFriendsTableView(){
        try {
            friendsList.setAll(superService.getFriendDtoOfUser(user.getId())); }
        catch (RepositoryException | SQLException | ValidationException e) {
            WarningBox.show(e.getMessage()); }
        friendsTableView.setItems(friendsList);
    }

    @Override
    public void observerUpdate() {
        updateFriendsTableView();
    }

    private void initializeFriendsTableView(){
        friendsTableColumnId.setCellValueFactory(new PropertyValueFactory<FriendDTO, Integer>("id"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("name"));
        friendsTableColumnFriendshipDate.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));
        updateFriendsTableView();
    }

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome " + user.getFirstName() + " " + user.getLastName() + "!");
        initializeFriendsTableView();
    }

    @FXML
    public void deleteFriendButtonAction(ActionEvent actionEvent){
        try{
            //delete from repository
            FriendDTO friendDto = friendsTableView.getSelectionModel().getSelectedItem();
            if (friendDto == null) {
                WarningBox.show("Select a friend to delete!");
                return;
            }
            Integer friendId = friendDto.getId();
            superService.deleteFriendship(user.getId(), friendId);
            //delete from table view if no exception was thrown
            friendsList.remove(friendDto);
            friendsTableView.setItems(friendsList);
        } catch (NumberFormatException e){
            WarningBox.show("The id must be an integer!");
        } catch (ValidationException | SQLException | RepositoryException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void changeToAddFriendScene(ActionEvent actionEvent){
        //changeToScene("resources/application/lab_6/addFriendScene.fxml", actionEvent);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AddFriendController addFriendController = new AddFriendController();
            addFriendController.setService(superService);
            addFriendController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("addFriendScene.fxml"));
            fxmlLoader.setController(addFriendController);
            Scene addFriendScene = new Scene(fxmlLoader.load());
            Stage addFriendStage = new Stage();
            addFriendStage.setTitle("The Network");
            addFriendStage.setScene(addFriendScene);
            addFriendStage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void friendRequestsView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            FriendRequestsController friendRequestsController = new FriendRequestsController();
            friendRequestsController.setService(superService);
            friendRequestsController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("friendRequestScene.fxml"));
            fxmlLoader.setController(friendRequestsController);
            Scene friendRequestScene = new Scene(fxmlLoader.load());
            Stage friendRequestStage = new Stage();
            friendRequestStage.setTitle("The Network");
            friendRequestStage.setScene(friendRequestScene);
            friendRequestStage.show();
            friendRequestWindow=friendRequestScene;
            //((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void backToLoginAction(ActionEvent actionEvent){
        try {
            NetworkController networkController = new NetworkController(superService);
            FXMLLoader fxmlLoader = new FXMLLoader(NetworkApplication.class.getResource("network.fxml"));
            fxmlLoader.setController(networkController);
            Scene loginScene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("The Network");
            stage.setScene(loginScene);
            stage.show();
            if (friendRequestWindow!=null)
                friendRequestWindow.getWindow().hide();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
            e.printStackTrace();
        }
    }

}
