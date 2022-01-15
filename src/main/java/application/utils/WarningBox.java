package application.utils;

import javafx.scene.control.Alert;

/**
 * Displays a warning in the UI
 */
public class WarningBox {

    /**
     * Displays a warning box with the given message
     * @param message String
     */
    public static void show(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
