package com.example.social_network_gui.controller;

import com.example.social_network_gui.domain.FriendRequest;
import com.example.social_network_gui.domain.RequestUserDTO;
import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.utils.Status;
import com.example.social_network_gui.utils.events.RequestsChangeEvent;
import com.example.social_network_gui.utils.observer.Observer;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManageFriendsController implements Observer<RequestsChangeEvent> {
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
    TableColumn<User, String> tableColumnNameSuggestions;
    @FXML
    TableColumn<User, Void> tableColumnButtons;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    TableView<User> tableViewUsers;
    @FXML
    Button removeButton;

    private ObservableList<RequestUserDTO> observableList = FXCollections.observableArrayList();
    @FXML
    public TableColumn<RequestUserDTO, Status> tableColumnStatus;
    @FXML
    public TableColumn<RequestUserDTO, String> tableColumnName;
    @FXML
    public TableView<RequestUserDTO> tableViewRequests;

    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<User> users = FXCollections.observableArrayList();
    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    public void setService(NetworkService service, FriendshipService friendshipService, UserService userService) {
        this.networkService = service;
        //this.networkService.addObserver(this);
        this.friendshipService = friendshipService;
        this.userService = userService;

        init();

        nameLabel.setText(networkService.getLoggedUser().getFirstName() + " " + networkService.getLoggedUser().getLastName());
        birthdateLabel.setText(networkService.getLoggedUser().getDate());
        switch (networkService.getLoggedUser().getGender()) {
            case "F" -> genderLabel.setText("Female");
            case "M" -> genderLabel.setText("Male");
            default -> genderLabel.setText("Other");
        }

        emailLabel.setText(networkService.getLoggedUser().getEmail());
    }

    private void init() {
        friends.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(networkService.getSuggestionsForLoggeduser());
        observableList.setAll(requestUserDTOS());


    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableColumnName.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, String>("name"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, Status>("status"));

        tableColumnNameSuggestions.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()));
        tableViewUsers.getColumns().add(tableColumnNameSuggestions);
        addButtonToTable();

        tableViewRequests.setItems(observableList);
        tableViewFriends.setItems(friends);
        tableViewUsers.setItems(users);
        tableViewUsers.setStyle("-fx-table-cell-border-color: transparent;");


    }

    private ArrayList<RequestUserDTO> requestUserDTOS() {
        Iterable<FriendRequest> friendships = networkService.friendshipIterable();
        List<FriendRequest> friendshipRequests = new ArrayList<FriendRequest>();

        friendshipRequests = StreamSupport.stream(friendships.spliterator(), false)
                .filter(x -> x.getId().getE2().getId().equals(networkService.getLoggedUser().getId()))
                .collect(Collectors.toList());
        ArrayList<Tuple<User, FriendRequest>> list = new ArrayList<Tuple<User, FriendRequest>>();
        friendshipRequests.forEach(x -> {
            list.add(new Tuple<>(userService.getUser(x.getId().getE1().getId()), x));
        });
        ArrayList<RequestUserDTO> requestListOfUserDTOS = new ArrayList<RequestUserDTO>();
        for (Tuple<User, FriendRequest> li : list) {
            String fullName = li.getE1().getFirstName() + " " + li.getE1().getLastName();
            RequestUserDTO requestUserDTO = new RequestUserDTO(li.getE2().getId(), fullName, li.getE2().getStatus());
            requestListOfUserDTOS.add(requestUserDTO);
        }
        return requestListOfUserDTOS;
    }

    public void accept_request(MouseEvent mouseEvent) {
        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus().equals(Status.PENDING)) {
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.acceptFriendRequest(request.getId().getE1().getId().toString());

            }
        }
        observableList.setAll(requestUserDTOS());
        friends.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(networkService.getSuggestionsForLoggeduser());
    }

    public void reject_request(MouseEvent mouseEvent) {
        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus().equals(Status.PENDING)) {
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.rejectFriendRequest(request.getId().getE1().getId().toString());
            }
        }
        observableList.setAll(requestUserDTOS());
        users.setAll(networkService.getSuggestionsForLoggeduser());
    }

    public void removeFriend(ActionEvent actionEvent) {
        User user = tableViewFriends.getSelectionModel().getSelectedItem();
        if (user != null) {
            try {

                if (friendshipService.getOne(new Tuple<>(user, networkService.getLoggedUser())).isPresent()) {
                    friendshipService.deleteFriendShip(new Tuple<>(user, networkService.getLoggedUser()));
                } else {
                    friendshipService.deleteFriendShip(new Tuple<>(networkService.getLoggedUser(), user));
                }

                tableViewFriends.getItems().removeAll(
                        tableViewFriends.getSelectionModel().getSelectedItem());
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }

        }
        users.setAll(networkService.getSuggestionsForLoggeduser());
    }

    public void addFriend(ActionEvent actionEvent) {
        User user = tableViewUsers.getSelectionModel().getSelectedItem();
        if (user != null) {
            try {
                networkService.sendFriendRequest(user.getId().toString());
                tableViewUsers.getItems().remove(user);
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }
        }
        observableList.setAll(requestUserDTOS());
    }

    public void logoutAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login-view.fxml"));

        AnchorPane root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setServices(userService, friendshipService, networkService);

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Social network");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        dialogStage.show();
        Stage stage = (Stage) removeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void update(RequestsChangeEvent requestsChangeEvent) {
        init();
    }
    private void addButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {

                final TableCell<User, Void> cell = new TableCell<User, Void>() {

                    private final Button btn = new Button();


                    {
                        btn.setOnAction((ActionEvent event) -> {
                            String text = btn.getText();
                            if (text.equals("ADD FRIEND")) {
                                networkService.sendFriendRequest(getTableView().getItems().get(getIndex()).getId().toString());
                                updateItem(null, false);
                                observableList.setAll(requestUserDTOS());
                            } else {
                                networkService.deleteRequest(new FriendRequest(new Tuple<>(networkService.getLoggedUser(),
                                        getTableView().getItems().get(getIndex())), Status.PENDING));
                                updateItem(null, false);

                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            {
                                Optional<FriendRequest> request = networkService.findOneRequest(new Tuple<>(networkService.getLoggedUser(),
                                        getTableView().getItems().get(getIndex())));
                                if (request.isPresent()) {
                                    btn.setText("CANCEL REQUEST");
                                    btn.setStyle("-fx-background-color: #EE964BFF; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                                } else {
                                    btn.setText("ADD FRIEND");
                                    btn.setStyle("-fx-background-color: #28AFB0FF; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                                }
                            }
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        tableColumnButtons.setCellFactory(cellFactory);

        tableViewUsers.getColumns().add(tableColumnButtons);


    }
}
