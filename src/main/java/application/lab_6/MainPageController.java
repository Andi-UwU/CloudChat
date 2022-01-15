package application.lab_6;

import application.domain.Event;
import application.domain.Message;
import application.domain.User;
import application.domain.FriendDTO;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.InfoBox;
import application.utils.WarningBox;
import application.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static application.utils.Constants.DATE_FORMATTER;
import static application.utils.Constants.DATE_TIME_FORMATTER;
import static java.time.temporal.ChronoUnit.DAYS;


public class MainPageController implements Observer {
    private SuperService superService;
    private User user;

    private Scene friendRequestWindow = null;
    private Scene addFriendWindow = null;

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

    //========= Activity button
    @FXML
    private Button activityButton;

    public MainPageController(User user, SuperService superService, Boolean noticed) {
        this.superService=superService;
        superService.addObserverForNetwork(this);
        this.user=user;
        this.noticed = noticed;
    }

    @FXML
    private Button chatButton;

    //========== Events
    @FXML
    private ListView<Event> eventListView;

    private ObservableList<Event> eventObservableList = FXCollections.observableArrayList();

    @FXML
    private Button subscribeButton;
    @FXML
    private Button unsubscribeButton;
    @FXML
    private Button createEventButton;

    @FXML
    private CheckBox subscribedCheckBox;

    @FXML
    private Label eventsLabel;

    private Boolean noticed = false;

    public void setNoticed(){
        noticed = true;
    }

    final class EventListCell extends ListCell<Event> {
        @Override
        protected void updateItem(Event event, boolean empty) {
            super.updateItem(event, empty);
            if (empty) {
                setGraphic(null);
            } else {

                // Create the VBox
                VBox eventBox = new VBox();
                // Title
                VBox titleBox = new VBox();
                Label title = new Label(event.getTitle());
                title.getStyleClass().add("event_title_text");
                titleBox.getChildren().add(title);
                titleBox.setAlignment(Pos.CENTER);
                // Description
                Label description = new Label(event.getDescription() + "\n\n" );
                description.getStyleClass().add("event_description_text");

                // Date of the event
                Label eventDate = new Label("Date: " + event.getEventDate().format(DATE_FORMATTER));
                eventDate.getStyleClass().add("event_details_text");
                // Author
                Label authorLabel = new Label("Author: " + event.getAuthor().getUserName());
                authorLabel.getStyleClass().add("event_details_text");
                // Subscribed/Unsubscribed
                String subscribedText;
                if (event.getSubscribers().contains(user))
                    subscribedText = "You are subscribed.";
                else
                    subscribedText = "You are not subscribed.";
                Label subscribedLabel = new Label(subscribedText);
                subscribedLabel.getStyleClass().add("event_details_text");
                // Events that will take place soon -> different background color
                //if (DAYS.between(LocalDate.now(), event.getEventDate()) < 2)
                //    eventBox.setBackground(new Background(new BackgroundFill(Color.valueOf("BFBFBF"), CornerRadii.EMPTY, Insets.EMPTY)));

                eventBox.getChildren().addAll(titleBox, description, eventDate, authorLabel, subscribedLabel);
                setGraphic(eventBox);
            }
        }
    }

    private void updateEventListView(){

        try {
            if (!subscribedCheckBox.isSelected())
                eventObservableList.setAll(superService.getEventsForUser(user));
            else
                eventObservableList.setAll(superService.getSubscribedEventsForUser(user));
            if (eventListView != null)
                eventListView.setItems(eventObservableList);
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void subscribedCheckBoxAction(ActionEvent actionEvent){

        try {
            if (subscribedCheckBox.isSelected()){
                eventObservableList.setAll(superService.getSubscribedEventsForUser(user));
                eventListView.setItems(eventObservableList);
            }
            else{
                eventObservableList.setAll(superService.getEventsForUser(user));
                eventListView.setItems(eventObservableList);
            }

        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }


    }

    @Override
    public void observerUpdate() {
        updateFriendsTableView();
    }


    private void updateFriendsTableView(){
        try {
            friendsList.setAll(superService.getFriendDtoOfUser(user.getId())); }
        catch (RepositoryException e) {
            WarningBox.show(e.getMessage()); }
        friendsTableView.setItems(friendsList);
    }

    private void initializeFriendsTableView(){
        friendsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //hides side scrollbar

        // Place Holder
        HBox placeHolderBox = new HBox();
        placeHolderBox.setAlignment(Pos.CENTER);
        Label placeHolderLabel = new Label("You have no friends =(");
        placeHolderLabel.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-weight: bold;");
        placeHolderBox.getChildren().add(placeHolderLabel);
        friendsTableView.setPlaceholder(placeHolderBox);

        friendsTableColumnId.setCellValueFactory(new PropertyValueFactory<FriendDTO, Integer>("id"));
        friendsTableColumnName.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("name"));
        friendsTableColumnFriendshipDate.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("date"));

        updateFriendsTableView();
    }
    @FXML
    private void initializeEventListView(){
        eventListView.setCellFactory(eventListView -> new EventListCell());

        // Place Holder
        HBox placeHolderBox = new HBox();
        placeHolderBox.setAlignment(Pos.CENTER);
        Label placeHolderLabel = new Label("There are no events available for you.\nTry to make some friends");
        placeHolderLabel.setStyle("-fx-font-size: 14px;\n" +
                "    -fx-text-fill: #FFFFFF;\n" +
                "    -fx-font-weight: bold;");
        placeHolderBox.getChildren().add(placeHolderLabel);
        eventListView.setPlaceholder(placeHolderBox);


        updateEventListView();

        try {
            String eventLabelText = "Events: ";
            Integer nr = superService.getNumberOfSoonEventsForUser(user);
            if (nr > 0){
                eventLabelText += "-> " + nr.toString() + " events will take place soon!";
                if (!noticed) {
                    InfoBox.show(nr.toString() + " events you are subscribed to will take place soon! " +
                            "\n Check your subscribed events to find more.");
                    noticed = true;
                }
            }
            eventsLabel.setText(eventLabelText);

        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void initialize() {

        welcomeLabel.setText("Welcome " + user.getUserName() + " (" + user.getFirstName() + " " + user.getLastName() + ") " + "!");
        initializeFriendsTableView();
        initializeEventListView();
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
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void subscribeButtonAction(ActionEvent actionEvent){
        try{
            Event selectedEvent = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                WarningBox.show("You have to select an event to subscribe to!");
                return;
            }
            superService.addSubscriber(selectedEvent, user);
            updateEventListView();
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void unsubscribeButtonAction(ActionEvent actionEvent){
        try{
            Event selectedEvent = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEvent == null) {
                WarningBox.show("You have to select an event to unsubscribe to!");
                return;
            }
            superService.removeSubscriber(selectedEvent, user);
            updateEventListView();
        } catch (RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void createEventButtonAction(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            CreateEventController createEventController = new CreateEventController();
            createEventController.setService(superService);
            createEventController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("createEventScene.fxml"));
            fxmlLoader.setController(createEventController);
            Scene createEventScene = new Scene(fxmlLoader.load());
            Stage createEventStage = new Stage();
            createEventStage.setTitle("Create Event!");
            createEventStage.setScene(createEventScene);
            createEventStage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }


    @FXML
    public void changeToAddFriendScene(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            AddFriendController addFriendController = new AddFriendController();
            addFriendController.setService(superService);
            addFriendController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("addFriendScene.fxml"));
            fxmlLoader.setController(addFriendController);
            Scene addFriendScene = new Scene(fxmlLoader.load());
            Stage addFriendStage = new Stage();
            addFriendStage.setTitle("Cloud Chat");
            addFriendStage.setScene(addFriendScene);
            addFriendStage.show();
            addFriendWindow = addFriendScene;
            //((Node)(actionEvent.getSource())).getScene().getWindow().hide();

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
            friendRequestStage.setTitle("Cloud Chat");
            friendRequestStage.setScene(friendRequestScene);
            friendRequestStage.show();

            friendRequestWindow=friendRequestScene;

            //((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
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
            stage.setTitle("Cloud Chat");
            stage.setScene(loginScene);
            stage.show();
            if (friendRequestWindow!=null)
                friendRequestWindow.getWindow().hide();
            if (addFriendWindow!=null)
                addFriendWindow.getWindow().hide();
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }


    @FXML
    public void changeToChatScene(ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            ChatController chatController = new ChatController();
            chatController.setService(superService);
            chatController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("chatScene.fxml"));
            fxmlLoader.setController(chatController);
            Scene chatScene = new Scene(fxmlLoader.load());
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat");
            chatStage.setScene(chatScene);
            chatStage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void changeToActivityScene(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            ActivityController activityController = new ActivityController();
            activityController.setService(superService);
            activityController.setUser(user);
            fxmlLoader.setLocation(getClass().getResource("activityScene.fxml"));
            fxmlLoader.setController(activityController);
            Scene activityScene = new Scene(fxmlLoader.load());
            Stage activityStage = new Stage();
            activityStage.setTitle("Activity");
            activityStage.setScene(activityScene);
            activityStage.show();
            //((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException | IOException e) {
            WarningBox.show(e.getMessage());
        }
    }
}
