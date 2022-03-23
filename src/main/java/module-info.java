module application.cloud_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires de.mkammerer.argon2;
    requires org.apache.pdfbox;

    opens application.cloud_chat to javafx.fxml;
    exports application.cloud_chat;
    exports application.domain;
    exports application.contoller;
    opens application.contoller to javafx.fxml;
}