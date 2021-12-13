package com.example.social_network_gui.controller;

import com.example.social_network_gui.Main;
import com.example.social_network_gui.controller.SignupController;
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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;


    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    @FXML
    private void initialize() {
    }


    public void setServices(UserService service, FriendshipService friendshipService, NetworkService networkService) {
        this.userService = service;
        this.friendshipService = friendshipService;
        this.networkService = networkService;

    }

    public void loginAction(ActionEvent actionEvent) {
        try {
            networkService.login(emailField.getText(), passwordField.getText());
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
        }

    }

    public void signupAction(ActionEvent actionEvent) {
        Main main = new Main();
        //main.changeScene("signup-view.fxml");
        try {
            FXMLLoader loader = new FXMLLoader();

            URL tess = getClass().getResource("signup-view.fxml");
            loader.setLocation(tess);

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign up");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SignupController ctrl = loader.getController();
            ctrl.setServices(networkService);

            dialogStage.show();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
