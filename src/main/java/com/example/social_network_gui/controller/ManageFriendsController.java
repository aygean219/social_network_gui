package com.example.social_network_gui.controller;

import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ManageFriendsController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label birthdateLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label emailLabel;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    TableColumn<User, String> tableColumnFirstName1;
    @FXML
    TableColumn<User, String> tableColumnLastName1;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    TableView<User> tableViewUsers;
    @FXML
    Button removeButton;


    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<User> users = FXCollections.observableArrayList();
    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    public void setService(NetworkService service, FriendshipService friendshipService) {
        this.networkService = service;
        this.friendshipService = friendshipService;
        friends.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(networkService.getSuggestionsForLoggeduser());

        nameLabel.setText(networkService.getLoggedUser().getFirstName() + " " + networkService.getLoggedUser().getLastName());
        birthdateLabel.setText(networkService.getLoggedUser().getDate());
        switch (networkService.getLoggedUser().getGender()) {
            case "F" -> genderLabel.setText("Female");
            case "M" -> genderLabel.setText("Male");
            default -> genderLabel.setText("Other");
        }

        emailLabel.setText(networkService.getLoggedUser().getEmail());
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnFirstName1.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName1.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableViewFriends.setItems(friends);
        tableViewUsers.setItems(users);


    }

    public void removeFriend(ActionEvent actionEvent) {
        User user = tableViewFriends.getSelectionModel().getSelectedItem();
        if (friendshipService.getOne(new Tuple<>(user, networkService.getLoggedUser())).isPresent())
            friendshipService.deleteFriendShip(new Tuple<>(user, networkService.getLoggedUser()));
        else
            friendshipService.deleteFriendShip(new Tuple<>(networkService.getLoggedUser(), user));
        tableViewFriends.getItems().removeAll(
                tableViewFriends.getSelectionModel().getSelectedItem());
        users.setAll(networkService.getSuggestionsForLoggeduser());
    }

    public void addFriend(ActionEvent actionEvent) {
        User user = tableViewUsers.getSelectionModel().getSelectedItem();
        networkService.sendFriendRequest(user.getId().toString());
        tableViewUsers.getItems().remove(user);
    }
}
