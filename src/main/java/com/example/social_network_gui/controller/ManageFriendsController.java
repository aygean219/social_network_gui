package com.example.social_network_gui.controller;

import com.example.social_network_gui.domain.*;
import com.example.social_network_gui.repository.memory.RepositoryException;
import com.example.social_network_gui.service.EventService;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManageFriendsController implements Observer<RequestsChangeEvent> {
    private static final int pageSize = 3;

    @FXML
    private Label nameLabel;
    @FXML
    private Label birthdateLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label emailLabel;
    @FXML
    TableColumn<UserRequestDTO, String> tableColumnNameSuggestions;
    @FXML
    TableColumn<UserRequestDTO, Void> tableColumnButtons;
    @FXML
    TableView<User> tableViewFriends;
    @FXML
    TableView<UserRequestDTO> tableViewUsers;
    @FXML
    ListView<User> listOfUsers;
    @FXML
    Button removeButton;
    @FXML
    TabPane tabPane;
    @FXML
    ListView<Event> listViewSuggestedEvents;
    @FXML
    ListView<Event> listViewUserEvents;
    @FXML
    Button createEventButton;
    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;
    @FXML
    private VBox vBoxMessage;
    @FXML
    private Button sendMessage;
    @FXML
    private Button goBack;
    @FXML
    private Button comingEvents;
    @FXML
    private TextField textMessage;
    @FXML
    private TextField groupName;
    @FXML
    private Pane replyArea;
    @FXML
    TextField pdfName;
    @FXML
    Pagination friendsPagination;
    @FXML
    public TableColumn<RequestUserDTO, Status> tableColumnStatus;
    @FXML
    public TableColumn<RequestUserDTO, String> tableColumnName;
    @FXML
    public TableView<RequestUserDTO> tableViewRequests;
    @FXML
    Label notificationsNrLabel;
    @FXML
    StackPane notificationsStackPane;
    ObservableList<Event> notifications = FXCollections.observableArrayList();

    private User userTo;
    private Message messageSelected;
    private Stage stage;


    private ObservableList<RequestUserDTO> requests = FXCollections.observableArrayList();


    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<UserRequestDTO> users = FXCollections.observableArrayList();
    ObservableList<User> chats = FXCollections.observableArrayList();
    ObservableList<Event> suggestedEvents = FXCollections.observableArrayList();
    ObservableList<Event> userEvents = FXCollections.observableArrayList();

    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;
    protected EventService eventService;

    public void setStage(Stage s) {
        this.stage = s;
    }

    public void setService(NetworkService service, FriendshipService friendshipService, UserService userService, EventService ev) {
        this.networkService = service;
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.eventService = ev;

        init();

        nameLabel.setText(networkService.getLoggedUser().getFirstName() + " " + networkService.getLoggedUser().getLastName());
        birthdateLabel.setText(networkService.getLoggedUser().getDate());
        switch (networkService.getLoggedUser().getGender()) {
            case "F" -> genderLabel.setText("Female");
            case "M" -> genderLabel.setText("Male");
            default -> genderLabel.setText("Other");
        }
        initializeEvents();
        emailLabel.setText(networkService.getLoggedUser().getEmail());

        double numberOfPages = Math.ceil(friends.size() / pageSize);
        if (numberOfPages < 1) numberOfPages = 1;
        friendsPagination.setPageCount((int) numberOfPages + 1);
        friendsPagination.setPageFactory(this::createPage);
        friendsPagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) ->
                createPage(newIndex.intValue()));

        updateNotificationLabel();
    }

    private void updateNotificationLabel() {
        if (notifications.size() > 0) {
            notificationsStackPane.setVisible(true);
            notificationsNrLabel.setText(String.valueOf(notifications.size()));
        } else notificationsStackPane.setVisible(false);
    }

    private Node createPage(Integer pageIndex) {

        ArrayList<User> friends = networkService.getFriendsOfLoggedUserOnPage(pageIndex, pageSize, networkService.getLoggedUser().getId());

        tableViewFriends.setItems(FXCollections.observableArrayList(friends));
        return tableViewFriends;

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
                    Text text;
                    if (msg.getReply() != null) {
                        text = new Text("Reply to: " + msg.getReply().getMessage() + "\n" + msg.getMessage());
                    } else {
                        text = new Text(msg.getMessage());
                    }
                    TextFlow textFlow = new TextFlow();
                    textFlow.setStyle("-fx-color: #1976D2 ; " + " -fx-background-color: #90CAF9;" + " -fx-background-radius :20px ;");
                    textFlow.setPadding(new Insets(5, 5, 5, 5));
                    text.setFill(Color.color(1, 1, 1));

                    textFlow.getChildren().add(text);
                    hBox.setPadding(new Insets(5));
                    hBox.getChildren().add(textFlow);
                    vBoxMessage.getChildren().add(hBox);
                } else {
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    Text text;
                    if (msg.getTo().getUsers().size() <= 1) {
                        if (msg.getReply() != null) {
                            text = new Text("Reply to: " + msg.getReply().getMessage() + "\n" + msg.getMessage());
                        } else {
                            text = new Text(msg.getMessage());
                        }
                    } else {
                        if (msg.getReply() != null) {
                            text = new Text("From " + msg.getTo().getName() + ": \n" + "Reply to: " + msg.getReply().getMessage() + "\n" + msg.getMessage());
                        } else {
                            text = new Text("From " + msg.getTo().getName() + ": \n" + msg.getMessage());
                        }
                    }

                    TextFlow textFlow = new TextFlow();
                    textFlow.setStyle("-fx-color: #1976D2 ; " + " -fx-background-color: #90CAF9;" + " -fx-background-radius :20px ;");
                    textFlow.setPadding(new Insets(5, 5, 5, 5));
                    text.setFill(Color.color(1, 1, 1));
                    EventHandler<MouseEvent> mouseEventHandler
                            = e -> {
                        messageSelected = msg;
                        textFlow.setStyle(" -fx-background-color: #1976D2;" + " -fx-background-radius :20px ;" + "-fx-border-color:  #0D47A1;" + "-fx-border-radius: 20px");
                    };
                    textFlow.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);//

                    EventHandler<MouseEvent> mouseEventHandler2
                            = e -> {
                        textFlow.setStyle("-fx-color: #1976D2 ; " + " -fx-background-color: #90CAF9;" + " -fx-background-radius :20px ;");
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

    private void initializeEvents() {
        suggestedEvents.setAll(eventService.getSuggestedEventsForUser(networkService.getLoggedUser()));
        userEvents.setAll(eventService.getEventsForUser(networkService.getLoggedUser()));
        listViewSuggestedEvents.setCellFactory(param -> new XCell("Subscribe", "-fx-background-color: #2196F3; -fx-text-fill:  #fff;-fx-border-color:  #90CAF9;-fx-border-width: 0 2 2 0;"));
        listViewUserEvents.setCellFactory(param -> new XCell("Unsubscribe", "-fx-background-color:  #ffccd5  ; -fx-text-fill: #800f2f; -fx-border-color: #800f2f;-fx-border-width: 0 2 2 0;"));

    }

    private void init() {
        friends.setAll(networkService.getFriendsOfLoggeduser());
        chats.setAll(networkService.getFriendsOfLoggeduser());
        users.setAll(suggestionsForLoggedUser());
        requests.setAll(requestUserDTOS());
        showMessages();
        notifications.setAll(eventService.getEventsForNotification(networkService.getLoggedUser()));


    }

    private TableView<User> createTableFriends() {
        TableView<User> friends = new TableView();
        friends.setPrefWidth(300);
        //friends.setPrefHeight(200);

        TableColumn<User, String> tableColumnFirstName = new TableColumn<>("First Name");
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnFirstName.setPrefWidth(150);
        tableColumnFirstName.setStyle("-fx-background-color: #BBDEFB");
        TableColumn<User, String> tableColumnLastName = new TableColumn<>("Last Name");
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnLastName.setPrefWidth(150);
        tableColumnLastName.setStyle("-fx-background-color: #BBDEFB");

        friends.getColumns().add(tableColumnFirstName);
        friends.getColumns().add(tableColumnLastName);

        TableColumn<User, Void> tableColumnButtonMessage = new TableColumn<>();

        tableColumnButtonMessage.setCellFactory(addButtonToTableForMessage());
        tableColumnButtonMessage.setPrefWidth(240);
        tableColumnButtonMessage.setStyle("-fx-background-color: #BBDEFB");

        friends.getColumns().add(tableColumnButtonMessage);
        return friends;
    }

    @FXML
    public void initialize() {

        tableColumnName.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, String>("name"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<RequestUserDTO, Status>("status"));

        tableColumnNameSuggestions.setCellValueFactory(cellData -> Bindings.createStringBinding(
                () -> cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName()));

        tableViewUsers.getColumns().add(tableColumnNameSuggestions);
        addButtonToTable();

        listOfUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listOfUsers.setCellFactory(param -> new FriendsCell());
        listOfUsers.setItems(chats);
        listOfUsers.setStyle("-fx-text-fill: #2196f3;");
        tableViewRequests.setItems(requests);

        tableViewUsers.setItems(users);
        listViewSuggestedEvents.setItems(suggestedEvents);
        listViewUserEvents.setItems(userEvents);
        tableViewUsers.setStyle("-fx-table-cell-border-color: transparent;");

        tableViewFriends = createTableFriends();
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
        createPage(friendsPagination.getCurrentPageIndex());
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
        loader.setLocation(getClass().getResource("login-winter-view.fxml"));

        BorderPane root = loader.load();

        SignUpAndLoginController ctrl = loader.getController();
        ctrl.setServices(userService, friendshipService, networkService, eventService);

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
        createPage(friendsPagination.getCurrentPageIndex());
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
                                    btn.setStyle("-fx-background-color:  #2196F3; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                                } else {
                                    btn.setText("CANCEL REQUEST");
                                    btn.setStyle("-fx-background-color: #ffccd5; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");

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

    public void sendAll(ActionEvent actionEvent) {
        ObservableList<User> userObservableList = listOfUsers.getSelectionModel().getSelectedItems();
        if (userObservableList != null) {
            try {
                List<User> list_to = userObservableList.stream().collect(Collectors.toList());
                String name_of_group = groupName.getText();
                String new_message = textMessage.getText();
                List<Long> id_to = new ArrayList<>();
                for (User u : list_to) {
                    id_to.add(u.getId());
                }
                networkService.send_message(id_to, name_of_group, new_message);
                textMessage.clear();
                groupName.clear();
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }
        }
        requests.setAll(requestUserDTOS());
    }

    @FXML
    public void handleNotifications() {
        StringBuilder text = new StringBuilder();
        for (Event event : notifications) {
            text.append("\n").append(event.getTitle().toUpperCase(Locale.ROOT)).append(" in ")
                    .append(ChronoUnit.DAYS.between(LocalDateTime.now(), event.getDateTime())).append(" days");
        }
        if (!text.toString().equals("")) {
            Notifications notification = Notifications.create()
                    .title("Upcoming events")
                    .text(text.toString())
                    .graphic(null)
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.CENTER);
            notification.showWarning();
        }
    }

    @FXML
    public void handleSendButton() {
        String new_message = textMessage.getText();
        List<Long> userList = new ArrayList<>();


        if (messageSelected != null) {
            networkService.reply_message(messageSelected.getId(), new_message);
        } else {
            userList.add(userTo.getId());
            networkService.send_message(userList, userTo.getFirstName() + " " + userTo.getLastName(), new_message);
        }
        textMessage.clear();
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);

        Text text = new Text(new_message);
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-color: #1976D2 ; " + " -fx-background-color: #90CAF9;" + " -fx-background-radius :20px ;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(1, 1, 1));

        textFlow.getChildren().add(text);
        hBox.setPadding(new Insets(5));
        hBox.getChildren().add(textFlow);
        vBoxMessage.getChildren().add(hBox);
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> addButtonToTableForMessage() {
        return new Callback<>() {
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
                            btn.setStyle("-fx-background-color:  #2196F3; -fx-border-color: #000000FF;-fx-border-width: 0 2 2 0;");
                        }
                    }
                };
                return cell;
            }
        };

    }


    public void handleSaveReport1() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        networkService.saveRaportToPDF(selectedDirectory.getAbsolutePath(), pdfName.getText() + ".pdf", startDate.getValue(), endDate.getValue());
        pdfName.clear();
        startDate.getEditor().clear();
        endDate.getEditor().clear();
    }

    public void handleSaveReport2(ActionEvent actionEvent) {
        User user = tableViewFriends.getSelectionModel().getSelectedItem();
        if (user != null) {
            try {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(stage);
                networkService.saveRaportToPDFForUser(selectedDirectory.getAbsolutePath(), pdfName.getText() + ".pdf", startDate.getValue(), endDate.getValue(), user);
                pdfName.clear();
                startDate.getEditor().clear();
                endDate.getEditor().clear();
            } catch (RepositoryException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public void createNewEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("createvent-view.fxml"));
            AnchorPane root = loader.load();
            CreateventView ctrl = loader.getController();
            ctrl.setService(eventService);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create event");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setOnCloseRequest(h -> suggestedEvents.setAll(eventService.getSuggestedEventsForUser(networkService.getLoggedUser())));
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class XCell extends ListCell<Event> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button button = new Button("");


        public XCell(String buttonText, String style) {
            super();
            button.setText(buttonText);
            button.setStyle(style);
            hbox.setStyle("-fx-alignment: center");
            hbox.getChildren().addAll(label, pane, button);
            label.setStyle("-fx-text-fill: #2196f3;");
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(e -> {
                if (buttonText.equals("Subscribe")) {

                    Event selected = getListView().getItems().get(getIndex());
                    ManageFriendsController.this.eventService.subscribeUser(selected, ManageFriendsController.this.networkService.getLoggedUser());


                    suggestedEvents.setAll(eventService.getSuggestedEventsForUser(networkService.getLoggedUser()));
                    userEvents.setAll(eventService.getEventsForUser(networkService.getLoggedUser()));
                    notifications.setAll(eventService.getEventsForNotification(networkService.getLoggedUser()));
                    updateNotificationLabel();

                    updateItem(selected, false);

                } else {
                    Event selected = getListView().getItems().get(getIndex());
                    ManageFriendsController.this.eventService.unsubscribeUser(selected, ManageFriendsController.this.networkService.getLoggedUser());

                    suggestedEvents.setAll(eventService.getSuggestedEventsForUser(networkService.getLoggedUser()));
                    userEvents.setAll(eventService.getEventsForUser(networkService.getLoggedUser()));
                    notifications.setAll(eventService.getEventsForNotification(networkService.getLoggedUser()));
                    updateNotificationLabel();

                    updateItem(selected, false);

                }
            });

        }

        @Override
        protected void updateItem(Event item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                setGraphic(null);
            } else {
                label.setText(item != null ? item.toString() : "<null>");
                setGraphic(hbox);

            }
        }
    }


    class FriendsCell extends ListCell<User> {
        Label label = new Label("");


        public FriendsCell() {
            super();
            label.setStyle("-fx-text-fill: #2196f3;");

        }

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                setGraphic(null);
            } else {
                label.setText(item != null ? item.toString() : "<null>");
                //label.setStyle("-fx-text-fill: #2196f3");
                setGraphic(label);

            }
        }
    }
}
