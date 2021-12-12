package application.utils;

import javafx.scene.control.Alert;

public class WarningBox {

    public static void show(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
