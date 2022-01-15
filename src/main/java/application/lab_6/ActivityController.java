package application.lab_6;

import application.domain.FriendDTO;
import application.domain.Message;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.InfoBox;
import application.utils.WarningBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ActivityController {
    private SuperService superService;
    private User user;
    private Integer currentFriendId;
    // ===========  Buttons  ============================
    @FXML
    private Button generateActivityButton;

    @FXML
    private Button backButton;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private Button resetButton;

    // =========== New Friends ==========================

    @FXML
    private TableView<FriendDTO> newFriendsTableView;

    @FXML
    private TableColumn<FriendDTO, Integer> newFriendsIdColumn;

    @FXML
    private TableColumn<FriendDTO, String> newFriendsNameColumn;

    // ===========  Messages List Fields ================
    @FXML
    private ListView<Message> chatMessageListView;

    @FXML
    private TextField messageTextField;

    private ObservableList<Message> messagesList = FXCollections.observableArrayList();

    @FXML
    private Label friendNameLabel;

    // ===========  Friends Table Fields ================

    @FXML
    private TableView<FriendDTO> chatFriendTableView;
    @FXML
    private TableColumn<FriendDTO, String> chatNameColumn;
    //@FXML
    //private TableColumn<FriendDTO, CheckBox> chatSelectColumn;
    @FXML
    private TableColumn<FriendDTO, Integer> chatIdColumn;

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();
    private ObservableList<FriendDTO> newFriendList = FXCollections.observableArrayList();

    // ===========  Setters  ================

    public void setService(SuperService superService){
        this.superService = superService;
    }

    public void setUser(User user){
        this.user = user;
    }

    @FXML
    public void initialize() {
        currentFriendId = 0;
        //initializeChatFriendsTableView();
        //initializeChatMessageListView();
    }

    // ===========  Friends Table  ================

    private void updateChatFriendsTableView(){
        try {
            friendsList.setAll(superService.getFriendDtoOfUser(user.getId()));
            chatFriendTableView.setItems(friendsList);

        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }
    
    private void updateNewFriendsTableView(LocalDate startDate, LocalDate endDate) {
        try {
            newFriendList.setAll(superService.generateFriendActivity(user.getId(),startDate,endDate));
            newFriendsTableView.setItems(newFriendList);

        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }
    
    final class CustomAlignmentListViewCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                // Create the HBox
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                if (item.getFrom().getId().equals(user.getId()))
                    hBox.setAlignment(Pos.BASELINE_RIGHT);
                else
                    hBox.setAlignment(Pos.BASELINE_LEFT);

                // Create centered Label
                Label label = new Label(item.toString());

                if (item.getFrom().getId().equals(user.getId()))
                    label.setAlignment(Pos.BASELINE_RIGHT);
                else
                    label.setAlignment(Pos.BASELINE_LEFT);

                hBox.getChildren().add(label);
                setGraphic(hBox);
            }
        }
    }

    private void initializeChatFriendsTableView(LocalDate start, LocalDate end){
        chatIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        chatNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        //chatSelectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        updateChatFriendsTableView();

        chatFriendTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendDTO> observable, FriendDTO oldValue, FriendDTO newValue) {
                if (newValue != null) {
                    if (oldValue != null)
                        if (oldValue.getId() == newValue.getId())
                            return;
                    updateMessageListView(newValue.getId(), start,end);
                }
            }
        });
    }

    private void initializeNewFriendsView(LocalDate start, LocalDate end) {
        newFriendsIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        newFriendsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        updateNewFriendsTableView(start, end);
    }

    // ===========  Messages  ================

    private void updateMessageListView(Integer userId, LocalDate start, LocalDate end){
        try {
            currentFriendId = userId;
            messagesList.setAll(superService.generateFriendMessageActivity
                    (user, superService.findUser(userId),start,end));
            chatMessageListView.setItems(messagesList);
            User friend = superService.findUser(userId);
            friendNameLabel.setText(friend.getFirstName() + " " + friend.getLastName());
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }


    private void initializeChatMessageListView(LocalDate start, LocalDate end){
        chatMessageListView.setCellFactory(messageListView -> new CustomAlignmentListViewCell());

        if (friendsList.size() > 0){
            currentFriendId = friendsList.get(0).getId();
            updateMessageListView(currentFriendId,start,end);
        }
        else
            friendNameLabel.setText("");
    }

    // =================== ACTIVITY

    private boolean verifyDates() {
        if (startDate.getValue() == null || endDate.getValue() == null) {
            WarningBox.show("Invalid date!");
            newFriendsTableView.getItems().clear();
            chatFriendTableView.getItems().clear();
            chatMessageListView.getItems().clear();
            startDate.setDisable(false);
            endDate.setDisable(false);
            return false;
        }
        return true;
    }

    @FXML
    private void generateActivityAction(ActionEvent event) {
        if (!verifyDates()) return;
        initializeNewFriendsView(startDate.getValue(),endDate.getValue());
        initializeChatFriendsTableView(startDate.getValue(),endDate.getValue());
        initializeChatMessageListView(startDate.getValue(),endDate.getValue());
        startDate.setDisable(true);
        endDate.setDisable(true);
    }

    @FXML
    private void exportAction (ActionEvent event) {
        if (!verifyDates()) return;
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
            if(selectedDirectory == null) {
                WarningBox.show("No directory selected!");
            }
            else {
                superService.generateActivityExportPDF(user, startDate.getValue(), endDate.getValue(),selectedDirectory.getAbsolutePath());
                InfoBox.show("PDF file successfully created!");
            }

        } catch (IOException | RepositoryException e) {
            WarningBox.show("Error exporting file.");
        }
    }

    @FXML
    private void backAction (ActionEvent actionEvent ){
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void resetAction (ActionEvent actionEvent) {
        startDate.setDisable(false);
        endDate.setDisable(false);
        startDate.setValue(null);
        endDate.setValue(null);
        newFriendsTableView.getItems().clear();
        chatFriendTableView.getItems().clear();
        chatMessageListView.getItems().clear();
    }
}