package application.lab_6;

import application.domain.AddFriendDTO;
import application.domain.User;
import application.exceptions.RepositoryException;
import application.exceptions.ValidationException;
import application.service.SuperService;
import application.utils.WarningBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class AddFriendController {

    private SuperService superService;
    private User user;

    // Table View
    @FXML
    private TableView<AddFriendDTO> tableView;
    @FXML
    private TableColumn<AddFriendDTO, Integer> idColumn;
    @FXML
    private TableColumn<AddFriendDTO, String> nameColumn;
    @FXML
    private TableColumn<AddFriendDTO, String> requestColumn;

    private ObservableList<AddFriendDTO> addFriendList = FXCollections.observableArrayList();

    //Buttons
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;

    public void setUser(User user){
        this.user = user;
    }
    public void setService(SuperService superService){
        this.superService = superService;
    }

    // Filter

    @FXML
    private Button filterButton;
    @FXML
    private TextField filterTextField;

    private void updateTableView(){
        try {
            addFriendList.setAll(superService.getAddFriendDtoOfUser(user.getId()));
        } catch (RepositoryException | SQLException | ValidationException e) {
            WarningBox.show(e.getMessage());
        }
        tableView.setItems(addFriendList);
    }
    private void initializeTableView(){
        idColumn.setCellValueFactory(new PropertyValueFactory<AddFriendDTO, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<AddFriendDTO, String>("name"));
        requestColumn.setCellValueFactory(new PropertyValueFactory<AddFriendDTO, String>("request"));
        updateTableView();

    }
    @FXML
    public void initialize(){
        initializeTableView();
    }

    @FXML
    public void addFriendRequest(ActionEvent actionEvent){
        try{
            AddFriendDTO addFriendDTO = tableView.getSelectionModel().getSelectedItem();
            if (addFriendDTO == null)
            {
                WarningBox.show("Select a user from the table!");
                return;
            }
            if (addFriendDTO.getRequest().equals("Already sent"))
            {
                WarningBox.show("A request was already sent");
                return;
            }

            Integer userId = addFriendDTO.getId();
            superService.addFriendRequest(user.getId(), userId);
            addFriendDTO.setRequest("Already sent");
            addFriendList.set(tableView.getSelectionModel().getSelectedIndex(), addFriendDTO);
            tableView.setItems(addFriendList);

        } catch (ValidationException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }

    }

    @FXML
    public void backButtonAction(ActionEvent actionEvent){
        try {
            /*FXMLLoader fxmlLoader = new FXMLLoader();
            MainPageController mainPageController = new MainPageController(user,superService);
            fxmlLoader.setLocation(getClass().getResource("mainpage.fxml"));
            fxmlLoader.setController(mainPageController);
            Scene mainScene = new Scene(fxmlLoader.load());
            Stage mainStage = new Stage();
            mainStage.setTitle("The Network");
            mainStage.setScene(mainScene);
            mainStage.show();*/
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (NumberFormatException e) {
            WarningBox.show(e.getMessage());
        }
    }

    @FXML
    public void filterButtonAction(ActionEvent actionEvent){
        try{
            String name = filterTextField.getText();
            addFriendList.setAll(superService.getAddFriendDtoOfUserByName(user.getId(), name));

            //tableView.setItems(addFriendList);

        } catch (ValidationException | SQLException | RepositoryException e) {
            WarningBox.show(e.getMessage());
        }
    }


}
