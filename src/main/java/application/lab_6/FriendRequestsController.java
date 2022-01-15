package application.lab_6;

import application.domain.FriendRequestDTO;
import application.domain.FriendRequestStatus;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import application.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class FriendRequestsController implements Observer {
    private SuperService superService;
    private User user;

    public void setUser(User user){
        this.user = user;
    }
    public void setService(SuperService superService){
        this.superService = superService;
        superService.addObserverForFriendRequests(this);
    }

    @FXML
    private TableView<FriendRequestDTO> requestsTableView;
    @FXML
    private TableColumn<FriendRequestDTO,Integer> idFromColumn;
    @FXML
    private TableColumn<FriendRequestDTO,String> nameFromColumn;
    @FXML
    private TableColumn<FriendRequestDTO,Integer> idToColumn;
    @FXML
    private TableColumn<FriendRequestDTO,String> nameToColumn;
    @FXML
    private TableColumn<FriendRequestDTO,FriendRequestStatus> statusColumn;
    @FXML
    private Button acceptButton;
    @FXML
    private Button declineButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button backButton;

    private ObservableList<FriendRequestDTO> friendRequestList = FXCollections.observableArrayList();

    private void updateTableView() {
        try {
            friendRequestList.setAll(superService.getAllFriendRequestsDtoForUser(user.getId()));
        }
        catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
        requestsTableView.setItems(friendRequestList);
    }

    @Override
    public void observerUpdate() {
        updateTableView();
    }

    private void initializeRequestsTableView() {
        idFromColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO,Integer>("idFrom"));
        nameFromColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("nameFrom"));
        idToColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, Integer>("idTo"));
        nameToColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("nameTo"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, FriendRequestStatus>("status"));
        updateTableView();
    }

    @FXML
    public void initialize() {
        initializeRequestsTableView();
    }

    @FXML
    public void acceptFriendRequestAction(ActionEvent actionEvent) {
        try {
            FriendRequestDTO friendRequestDTO = requestsTableView.getSelectionModel().getSelectedItem();
            if (friendRequestDTO==null) {
                WarningBox.show("Nothing was selected from the table");
                return;
            }
            if (friendRequestDTO.getNameFrom().equals("You")) {
                WarningBox.show("That request isn't yours to accept!");
                return;
            }
            superService.updateFriendRequest(friendRequestDTO.getIdFrom(),friendRequestDTO.getIdTo(),"ACCEPTED");
            updateTableView();
        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void declineFriendRequestAction(ActionEvent actionEvent) {
        try {
            FriendRequestDTO friendRequestDTO = requestsTableView.getSelectionModel().getSelectedItem();
            if (friendRequestDTO==null) {
                WarningBox.show("Nothing was selected from the table");
                return;
            }
            if (friendRequestDTO.getNameFrom().equals("You")) {
                WarningBox.show("That request isn't yours to decline!");
                return;
            }
            if (friendRequestDTO.getStatus().equals(FriendRequestStatus.ACCEPTED)) {
                WarningBox.show("You can't decline an accepted request!");
                return;
            }

            superService.updateFriendRequest(friendRequestDTO.getIdFrom(),friendRequestDTO.getIdTo(),"DECLINED");
            updateTableView();
        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void cancelFriendRequestAction(ActionEvent actionEvent) {
        try {
            FriendRequestDTO friendRequestDTO = requestsTableView.getSelectionModel().getSelectedItem();
            if (friendRequestDTO==null) {
                WarningBox.show("Nothing was selected from the table");
                return;
            }
            if (friendRequestDTO.getNameTo().equals("You")) {
                WarningBox.show("That request isn't yours to cancel!");
                return;
            }
            superService.deleteFriendRequest(friendRequestDTO.getIdFrom(),friendRequestDTO.getIdTo());
            updateTableView();
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void backMenuAction(ActionEvent actionEvent){
        try {
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            WarningBox.show(e.getMessage());
        }
    }

}
