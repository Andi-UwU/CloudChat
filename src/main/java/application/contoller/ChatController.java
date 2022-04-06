package application.contoller;

import application.domain.FriendDTO;
import application.domain.Message;
import application.domain.User;
import application.domain.UserDTO;
import application.exceptions.RepositoryException;
import application.exceptions.ServiceException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.SceneChanger;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatController implements Controller {
    private SuperService superService;
    private User user;
    private Integer currentFriendId;
    private Integer currentPage;

    // ===========  Messages List Fields ================
    @FXML
    private ListView<Message> chatMessageListView;

    @FXML
    private TextField messageTextField;

    private ObservableList<Message> messagesList = FXCollections.observableArrayList();

    @FXML
    private Label friendNameLabel;

    @FXML
    private Label pageNumberLabel;

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

    @FXML
    private Button previousPage;

    @FXML
    private Button nextPage;

    // ===========  Setters  ================

    public void setService(SuperService superService){
        this.superService = superService;
    }

    public void setUser(User user){
        this.user = user;
    }

    @FXML
    public void initialize() {

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


    private void initializeChatFriendsTableView(){
        chatIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        chatNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        chatSelectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        updateChatFriendsTableView();

        chatFriendTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FriendDTO>() {
            @Override
            public void changed(ObservableValue<? extends FriendDTO> observable, FriendDTO oldValue, FriendDTO newValue) {
                try {
                    User friend = superService.findUser(newValue.getId());
                    currentPage = superService.getNumberOfConversationPages(user,friend);
                } catch (RepositoryException ignored) { }
                pageNumberLabel.setText(currentPage.toString());
                if (newValue != null) {
                    if (oldValue != null) {
                        if (oldValue.getId() == newValue.getId())
                            return;
                    }
                    updateMessageListView(newValue.getId(), currentPage);
                }
            }
        });
    }

    @Override
    public void initializeController(SuperService superService, Optional<User> user) {
        this.superService = superService;
        this.user = user.get();

        currentFriendId = 0;
        currentPage = 1;
        initializeChatFriendsTableView();
        initializeChatMessageListView();
        pageNumberLabel.setText(currentPage.toString());
    }

    // ===========  Messages  ================

    // Custom class for messages
    final class MessageListViewCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                // Create  Label
                Label label = new Label(item.toString());
                // Create the HBox
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                if (item.getFrom().getId().equals(user.getId())) { //sent by user
                    hBox.setAlignment(Pos.BASELINE_RIGHT);
                    label.getStyleClass().add("message_user");
                }
                else { //sent by friend
                    hBox.setAlignment(Pos.BASELINE_LEFT);

                    label.getStyleClass().add("message_friend");
                }
                hBox.getChildren().add(label);
                setGraphic(hBox);
            }
        }
    }
    private void updateMessageListView(Integer userId, Integer page){
        try {
            currentFriendId = userId;
            User friend = superService.findUser(userId);
            messagesList.setAll(superService.getConversationPage(user, friend, page));
            chatMessageListView.setItems(messagesList);

            friendNameLabel.setText(friend.getFirstName() + " " + friend.getLastName());
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }


    private void initializeChatMessageListView() {
        chatMessageListView.setCellFactory(messageListView -> new MessageListViewCell());
        try {
            if (friendsList.size() > 0){
                currentFriendId = friendsList.get(0).getId();
                currentPage = 0;

                currentPage = superService.getNumberOfConversationPages(user, superService.findUser(currentFriendId));

                updateMessageListView(currentFriendId, currentPage);
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
                    }
                }
            });
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    // ===========  Buttons Actions  ================

    @FXML
    private void backButtonAction(ActionEvent actionEvent){
        try {
            SceneChanger.changeTo(actionEvent, "mainpage.fxml", new MainPageController(), superService, Optional.of(user));
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
                //messagesList.add(message);
                //chatMessageListView.setItems(messagesList);
                updateMessageListView(currentFriendId, currentPage);
                messageTextField.clear();
            } catch (ValidationException | RepositoryException e) {
                WarningBox.show(e.getMessage());
            }
        }

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
        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }

        updateMessageListView(currentFriendId, currentPage);
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
            updateMessageListView(currentFriendId,currentPage);
        } catch (ServiceException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    private void previousPageAction(ActionEvent actionEvent){
        if (currentPage > 1 && !currentFriendId.equals(0)) {
            currentPage--;
            updateMessageListView(currentFriendId, currentPage);
            pageNumberLabel.setText(currentPage.toString());
        }

    }
    @FXML
    private void nextPageAction(ActionEvent actionEvent){
        try {
            if (!currentFriendId.equals(0)) {
                User currentFriend = superService.findUser(currentFriendId);
                if (currentPage < superService.getNumberOfConversationPages(user, currentFriend)) {
                    currentPage++;
                    updateMessageListView(currentFriendId, currentPage);
                    pageNumberLabel.setText(currentPage.toString());
                }
            }
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }

    }
}
