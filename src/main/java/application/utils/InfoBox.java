package application.utils;

import javafx.scene.control.Alert;

/**
 * Displays an information box in the UI
 */
public class InfoBox {

    /**
     * Displays an information box with a given message
     * @param message String
     */
    public static void show(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.showAndWait();
    }

}