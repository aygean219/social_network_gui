package com.example.social_network_gui.controller;

import com.example.social_network_gui.domain.*;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    TableColumn<UserRequestDTO, String> tableColumnNameSuggestions;
    @FXML
    TableColumn<UserRequestDTO, Void> tableColumnButtons;
    @FXML
    TableColumn<User, Void> tableColumnButtonMessage;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    TableView<UserRequestDTO> tableViewUsers;
    @FXML
    Button removeButton;
    @FXML
    TabPane tabPane;

    ///
    private User userTo;
    private Message messageSelected;
    @FXML
    private VBox vBoxMessage;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button sendMessage;
    @FXML
    private Button goBack;
    @FXML
    private TextField textMessage;
    @FXML
    private Pane replyArea;

    /////

    private ObservableList<RequestUserDTO> requests = FXCollections.observableArrayList();
    @FXML
    public TableColumn<RequestUserDTO, Status> tableColumnStatus;
    @FXML
    public TableColumn<RequestUserDTO, String> tableColumnName;
    @FXML
    public TableView<RequestUserDTO> tableViewRequests;

    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<UserRequestDTO> users = FXCollections.observableArrayList();
    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    public void setService(NetworkService service, FriendshipService friendshipService, UserService userService) {
        this.networkService = service;
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

    public void setUserTo(User to) {
        this.userTo = to;
        vBoxMessage.getChildren().clear();
    }

    private void showMessages() {
        if (this.userTo != null) {
            for (Message msg : networkService.cronological_message(userTo.getId())) {
                HBox hBox = new HBox();
                if (msg.getFrom().getId() == networkService.getLoggedUser().getId()) {
                    hBox.setAlignment(Pos.CENTER_RIGHT);//my messages are in right
                    Text text = new Text(msg.getMessage());
                    TextFlow textFlow = new TextFlow();
                    textFlow.setStyle("-fx-color: #53A2BE ; " + " -fx-background-color: #0A2239;" + " -fx-background-radius :20px ;");
                    textFlow.setPadding(new Insets(5, 5, 5, 5));
                    text.setFill(Color.color(1, 1, 1));

                    textFlow.getChildren().add(text);
                    hBox.setPadding(new Insets(5));
                    hBox.getChildren().add(textFlow);
                    vBoxMessage.getChildren().add(hBox);
                } else {
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    Text text = new Text(msg.getMessage());
                    TextFlow textFlow = new TextFlow();
                    textFlow.setStyle("-fx-color: #53A2BE ; " + " -fx-background-color: #0A2239;" + " -fx-background-radius :20px ;");
                    textFlow.setPadding(new Insets(5, 5, 5, 5));
                    text.setFill(Color.color(1, 1, 1));
                    EventHandler<MouseEvent> mouseEventHandler
                            = e -> {
                        messageSelected = msg;
                        textFlow.setStyle(" -fx-background-color: #53A2BE;" + " -fx-background-radius :20px ;" + "-fx-border-color:  #0A2239;" + "-fx-border-radius: 20px");
                    };
                    textFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);//

                    EventHandler<MouseEvent> mouseEventHandler2
                            = e -> {
                        textFlow.setStyle("-fx-color: #53A2BE ; " + " -fx-background-color: #0A2239;" + " -fx-background-radius :20px ;");
                    };
                    sendMessage.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler2);
                    hBox.setPadding(new Insets(5));
                    textFlow.getChildren().add(text);
                    hBox.getChildren().add(textFlow);
                    vBoxMessage.getChildren().add(hBox);
                }

            }
        }
    }

    private void init() {
        friends.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(suggestionsForLoggedUser());
        requests.setAll(requestUserDTOS());
        showMessages();

    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableColumnName.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, String>("name"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, Status>("status"));

        tableColumnNameSuggestions.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName()));

        tableViewUsers.getColumns().add(tableColumnNameSuggestions);
        addButtonToTable();
        addButtonToTableForMessage();
        tableViewRequests.setItems(requests);
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

    private ArrayList<UserRequestDTO> suggestionsForLoggedUser() {
        List<User> suggestions = networkService.getSuggestionsForLoggeduser();

        ArrayList<Tuple<User, FriendRequest>> userRequests = new ArrayList<>();
        suggestions.forEach(x -> {
            FriendRequest request = networkService.getRequest(new Tuple<>(networkService.getLoggedUser(), x));
            userRequests.add(new Tuple<>(x, request));
        });

        ArrayList<UserRequestDTO> suggestionsDTO = new ArrayList<>();
        userRequests.forEach(x -> {
            UserRequestDTO requestUserDTO;
            if (x.getE2() == null)
                requestUserDTO = new UserRequestDTO(x.getE1(), null);
            else
                requestUserDTO = new UserRequestDTO(x.getE1(), x.getE2().getStatus());
            suggestionsDTO.add(requestUserDTO);
        });

        return suggestionsDTO;
    }

    public void accept_request(MouseEvent mouseEvent) {
        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus().equals(Status.PENDING)) {
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.acceptFriendRequest(request.getId().getE1().getId().toString());

            }
        }
        requests.setAll(requestUserDTOS());
        friends.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(suggestionsForLoggedUser());
    }

    public void reject_request(MouseEvent mouseEvent) {
        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus().equals(Status.PENDING)) {
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.rejectFriendRequest(request.getId().getE1().getId().toString());
            }
        }
        requests.setAll(requestUserDTOS());
        users.setAll(suggestionsForLoggedUser());
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
        users.setAll(suggestionsForLoggedUser());
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
        Callback<TableColumn<UserRequestDTO, Void>, TableCell<UserRequestDTO, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<UserRequestDTO, Void> call(final TableColumn<UserRequestDTO, Void> param) {

                final TableCell<UserRequestDTO, Void> cell = new TableCell<UserRequestDTO, Void>() {

                    private final Button btn = new Button();


                    {
                        btn.setOnAction((ActionEvent event) -> {
                            String text = btn.getText();
                            if (text.equals("ADD FRIEND")) {
                                networkService.sendFriendRequest(getTableView().getItems().get(getIndex()).getUser().getId().toString());
                                getTableView().getItems().get(getIndex()).setStatus(Status.PENDING);
                                updateItem(null, false);
                                requests.setAll(requestUserDTOS());
                            } else {
                                networkService.deleteRequest(new FriendRequest(new Tuple<>(networkService.getLoggedUser(),
                                        getTableView().getItems().get(getIndex()).getUser()), Status.PENDING));
                                getTableView().getItems().get(getIndex()).setStatus(null);
                                updateItem(null, false);
                                requests.setAll(requestUserDTOS());
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
                                Status x = getTableView().getItems().get(getIndex()).getStatus();
                                if (x == null) {
                                    btn.setText("ADD FRIEND");
                                    btn.setStyle("-fx-background-color: #28AFB0FF; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                                } else {
                                    btn.setText("CANCEL REQUEST");
                                    btn.setStyle("-fx-background-color: #EE964BFF; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");

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

        //tableViewUsers.getColumns().add(tableColumnButtons);


    }

    @FXML
    public void handleSendButton() {
        String new_message = textMessage.getText();
        List<Long> userList = new ArrayList<>();
        userList.add(userTo.getId());

        if (messageSelected != null) {
            networkService.reply_message(messageSelected.getId(), new_message);
        } else {
            networkService.send_message(userList, new_message);
        }
        textMessage.clear();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);

        Text text = new Text(new_message);
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-color: #53A2BE ; " + " -fx-background-color: #0A2239;" + " -fx-background-radius :20px ;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(1, 1, 1));

        textFlow.getChildren().add(text);
        hBox.setPadding(new Insets(5));
        hBox.getChildren().add(textFlow);
        vBoxMessage.getChildren().add(hBox);
    }

    private void addButtonToTableForMessage() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {

                final TableCell<User, Void> cell = new TableCell<User, Void>() {

                    private final Button btn = new Button("Send Message");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            User user_data = getTableView().getItems().get(getIndex());
                            tabPane.getSelectionModel().select(3);
                            setUserTo(user_data);
                            showMessages();

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            btn.setStyle("-fx-background-color: #28AFB0FF; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                        }
                    }
                };
                return cell;
            }
        };

        tableColumnButtonMessage.setCellFactory(cellFactory);
        tableViewFriends.getColumns().add(tableColumnButtonMessage);
    }

}
