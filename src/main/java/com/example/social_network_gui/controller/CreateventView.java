package com.example.social_network_gui.controller;

import com.example.social_network_gui.service.EventService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    }
}
