package com.example.social_network_gui.controller;

import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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


    public void setServices(UserService service, FriendshipService friendshipService, NetworkService networkService ) {
        this.userService = service;
        this.friendshipService = friendshipService;
        this.networkService = networkService;

    }

    public void loginAction(ActionEvent actionEvent) throws Exception {
        try{
        networkService.login(emailField.getText(),passwordField.getText());
        }
        catch (ValidationException e){
            errorLabel.setText(e.getMessage());
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/social_network_gui/menu-view.fxml"));
        Pane root;
        root = loader.load();


        Stage register_stage=new Stage();
        MenuMainController menuMainController=loader.getController();
        menuMainController.initial(userService,friendshipService,networkService);
        register_stage.setScene(new Scene(root));
        register_stage.show();
    }

}
