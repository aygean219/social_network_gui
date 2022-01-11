package com.example.social_network_gui.controller;

import com.example.social_network_gui.service.EventService;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CreateventView {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField locationField;
    @FXML
    private Button createButton;

    private EventService eventService;

    public void setService(EventService eventService) {
        this.eventService = eventService;

    }

    public void createAction(ActionEvent actionEvent) {
        eventService.addEvent(titleField.getText(),descriptionField.getText(),locationField.getText(),datePicker.getValue().atStartOfDay());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("The event has been created!");

        alert.showAndWait();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }
}
