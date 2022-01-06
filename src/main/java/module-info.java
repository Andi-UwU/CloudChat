module application.lab_6 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires de.mkammerer.argon2;

    opens application.lab_6 to javafx.fxml;
    exports application.lab_6;
    exports application.domain;
}