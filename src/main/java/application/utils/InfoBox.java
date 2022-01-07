package application.utils;

import javafx.scene.control.Alert;

public class InfoBox {

    public static void show(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.showAndWait();
    }

}