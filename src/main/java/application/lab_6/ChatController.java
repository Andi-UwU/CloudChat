package application.lab_6;

import application.domain.FriendDTO;
import application.domain.Message;
import application.domain.User;
import application.domain.UserDTO;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ChatController {
    private SuperService superService;
    private User user;
    private Integer currentFriendId;

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
    @FXML
    private TableColumn<FriendDTO, CheckBox> chatSelectColumn;
    @FXML
    private TableColumn<FriendDTO, Integer> chatIdColumn;

    private ObservableList<FriendDTO> friendsList = FXCollections.observableArrayList();

    // ===========  Sent To Table Fields  ================

    @FXML
    private TableView<UserDTO> sentToTableView;

    @FXML
    private TableColumn<UserDTO, Integer> sentToColumnId;

    @FXML
    private TableColumn<UserDTO, String> sentToColumnName;

    private ObservableList<UserDTO> sentToList = FXCollections.observableArrayList();


    // ===========  Buttons Fields  ================

    @FXML
    private Button sendMessageButton;

    @FXML
    private Button backButton;

    @FXML
    private Button replyButton;

    @FXML
    private Button replyToAllButton;

    @FXML
    private Button sendToSelectedButton;

    @FXML
    private Button deleteMessageButton;

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
        initializeChatFriendsTableView();
        initializeChatMessageListView();
        initializeSentToTable();
    }

    // ===========  Friends Table  ================

    private void updateChatFriendsTableView(){
        try {
            friendsList.setAll(superService.getFriendDtoOfUser(user.getId()));
            chatFriendTableView.setItems(friendsList);

        } catch (RepositoryException | SQLException | ValidationException e) {
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

    private void initializeChatFriendsTableView(){
        chatIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        chatNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        chatSelectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        updateChatFriendsTableView();

        chatFriendTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendDTO> observable, FriendDTO oldValue, FriendDTO newValue) {
                if (newValue != null) {
                    if (oldValue != null)
                        if (oldValue.getId() == newValue.getId())
                            return;
                    updateMessageListView(newValue.getId());
                }
            }
        });
    }

    // ===========  Messages  ================

    private void updateMessageListView(Integer userId){
        try {
            currentFriendId = userId;
            messagesList.setAll(superService.getConversation(user.getId(), userId));
            chatMessageListView.setItems(messagesList);
            User friend = superService.findUser(userId);
            friendNameLabel.setText(friend.getFirstName() + " " + friend.getLastName());
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }


    private void initializeChatMessageListView(){
        chatMessageListView.setCellFactory(messageListView -> new CustomAlignmentListViewCell());

        if (friendsList.size() > 0){
            currentFriendId = friendsList.get(0).getId();
            updateMessageListView(currentFriendId);
        }
        else
            friendNameLabel.setText("");

        chatMessageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Message>() {
            @Override
            public void changed(ObservableValue<? extends Message> observable, Message oldValue, Message newValue) {
                if (newValue != null) {
                    if (oldValue != null) {
                        if (oldValue.getId() == newValue.getId())
                            return;
                    }
                    updateSentToTable();
                }
            }
        });
    }

    // ===========  Sent To Table  ================

    private void initializeSentToTable(){
        sentToColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        sentToColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

    }
    private void updateSentToTable(){
        Message selectedMessage = chatMessageListView.getSelectionModel().getSelectedItem();
        if (selectedMessage == null){
            sentToList.clear();
        }
        else{
            sentToList.setAll(superService.getUserDtoOfMessage(selectedMessage));
        }
        sentToTableView.setItems(sentToList);
    }

    // ===========  Buttons Actions  ================

    @FXML
    private void backButtonAction(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            MainPageController mainPageController = new MainPageController(user,superService);
            fxmlLoader.setLocation(getClass().getResource("mainpage.fxml"));
            fxmlLoader.setController(mainPageController);
            Scene mainScene = new Scene(fxmlLoader.load());
            Stage mainStage = new Stage();
            mainStage.setTitle("The Network");
            mainStage.setScene(mainScene);
            mainStage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    private void sendButtonAction(ActionEvent actionEvent){
        String text = messageTextField.getText();
        if (text.equals("")){
            WarningBox.show("You have to write a message!");
        }
        else{
            try {
                Message message = superService.addMessage(user.getId(), List.of(currentFriendId), text);
                messagesList.add(message);
                chatMessageListView.setItems(messagesList);
            } catch (ValidationException | SQLException | RepositoryException | IOException e) {
                WarningBox.show(e.getMessage());
            }
        }
        messageTextField.clear();
    }

    @FXML
    private void replyButtonAction(ActionEvent actionEvent){
        String text = messageTextField.getText();
        if (text.equals("")){
            WarningBox.show("You have to write a message!");
            return;
        }
        Message message = chatMessageListView.getSelectionModel().getSelectedItem();
        if (message == null){
            WarningBox.show("You have to select a message to reply to!");
            return;
        }
        try {
            superService.addReply(user.getId(), message.getId(), text);
        } catch (ValidationException | SQLException | RepositoryException | IOException e) {
            WarningBox.show(e.getMessage());
        }

        updateMessageListView(currentFriendId);
        messageTextField.clear();
    }

    @FXML
    private void replyToAllButtonAction(ActionEvent actionEvent){
        String text = messageTextField.getText();
        if (text.equals("")){
            WarningBox.show("You have to write a message!");
            return;
        }
        Message message = chatMessageListView.getSelectionModel().getSelectedItem();
        if (message == null){
            WarningBox.show("You have to select a message to reply to!");
            return;
        }
        try {
            superService.addReplyToAll(user.getId(), message.getId(), text);
        } catch (ValidationException | SQLException | RepositoryException | IOException e) {
            WarningBox.show(e.getMessage());
        }

        updateMessageListView(currentFriendId);
        messageTextField.clear();
    }

    @FXML
    private void sendToSelectedButtonAction(ActionEvent actionEvent){
        String text = messageTextField.getText();
        if (text.equals("")){
            WarningBox.show("You have to write a message!");
        }
        else{
            try {
                List<Integer> toIds = friendsList
                        .stream()
                        .filter(friendDTO -> {return friendDTO.getSelect().isSelected();})
                        .map(friendDTO -> {return friendDTO.getId();})
                        .collect(Collectors.toList());
                superService.addMessage(user.getId(), toIds, text);
            } catch (ValidationException | SQLException | RepositoryException | IOException e) {
                WarningBox.show(e.getMessage());
            }
        }
        updateMessageListView(currentFriendId);
        messageTextField.clear();
    }

    @FXML
    private void deleteMessageButtonAction(ActionEvent actionEvent){
        Message selectedMessage = chatMessageListView.getSelectionModel().getSelectedItem();
        if (selectedMessage == null){
            WarningBox.show("You have to select a message to delete!");
            return;
        }
        try {
            superService.userDeleteMessage(user, selectedMessage.getId());
            messagesList.remove(selectedMessage);
            chatMessageListView.setItems(messagesList);
        } catch (ValidationException | SQLException |  RepositoryException| ServiceException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }
}