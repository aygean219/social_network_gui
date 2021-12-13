package com.example.social_network_gui.controller;

import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;

public class SignupController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox genderCombobox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;


    private NetworkService networkService;

    public void setServices(NetworkService networkService) {
        this.networkService = networkService;

    }

    public void signupAction(ActionEvent actionEvent) {
        String gender;
        Object value = genderCombobox.getValue();
        if ("Male".equals(value)) {
            gender = "M";
        } else if ("Female".equals(value)) {
            gender = "F";
        } else {
            gender = "other";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = formatter.format(datePicker.getValue());
        try {
            networkService.signup(firstNameField.getText(), lastNameField.getText(), date, gender, emailField.getText(), passwordField.getText());
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
