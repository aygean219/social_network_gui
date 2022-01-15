package com.example.social_network_gui.controller;

import com.example.social_network_gui.Main;
import com.example.social_network_gui.service.EventService;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class SignUpAndLoginController {
    @FXML
    private TextField emailField1;
    @FXML
    private PasswordField passwordField1;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel1;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox genderCombobox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField passwordField2;
    @FXML
    private TextField emailField2;
    @FXML
    private Label errorLabel2;

    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;
    private EventService eventService;

    public static void openAnotherScene(String sceneFXMLFile, String boxTitle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(sceneFXMLFile));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(boxTitle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(){
        genderCombobox.getItems().addAll("Male","Female","Other");
    }
    public void setServices(UserService service, FriendshipService friendshipService, NetworkService networkService, EventService ev) {
        this.userService = service;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
        this.eventService = ev;

    }

    public void loginAction(ActionEvent actionEvent) throws IOException {
        try {
            networkService.login(emailField1.getText(), passwordField1.getText());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("managefriends-view.fxml"));


            AnchorPane root = loader.load();

            ManageFriendsController ctrl = loader.getController();
            ctrl.setService(networkService,friendshipService,userService,eventService);


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Winternet");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            ctrl.setStage(dialogStage);
            dialogStage.show();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
        } catch (ValidationException e) {
            errorLabel1.setText(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            networkService.signup(firstNameField.getText(), lastNameField.getText(), date, gender, emailField2.getText(), passwordField2.getText());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("managefriends-view.fxml"));


            AnchorPane root = loader.load();

            ManageFriendsController ctrl = loader.getController();
            ctrl.setService(networkService, friendshipService,userService,eventService);


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Winternet");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            dialogStage.show();
            Stage stage = (Stage) errorLabel2.getScene().getWindow();
            stage.close();
        } catch (ValidationException e) {
            errorLabel2.setText(e.getMessage());
        }
    }
}
