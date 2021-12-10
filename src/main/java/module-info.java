module com.example.social_network_gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.social_network_gui to javafx.fxml;
    opens com.example.social_network_gui.controller to javafx.fxml;
    exports com.example.social_network_gui;
    exports com.example.social_network_gui.controller;
}