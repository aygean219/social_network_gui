package com.example.social_network_gui.controller;

import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    private FriendshipService friendshipService;
    private UserService userService;

    public void setServices(NetworkService networkService, FriendshipService friendshipService,UserService userService) {
        this.networkService = networkService;
        this.friendshipService = friendshipService;
        this.userService = userService;

    }

    public void signupAction(ActionEvent actionEvent) throws IOException {
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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("managefriends-view.fxml"));


            AnchorPane root = loader.load();

            ManageFriendsController ctrl = loader.getController();
            ctrl.setService(networkService, friendshipService,userService);


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            dialogStage.show();
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            stage.close();
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
