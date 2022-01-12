package com.example.social_network_gui;

import com.example.social_network_gui.controller.LoginController;
import com.example.social_network_gui.domain.*;
import com.example.social_network_gui.repository.Repository;
import com.example.social_network_gui.repository.database.*;
import com.example.social_network_gui.service.EventService;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.validators.FriendshipValidator;
import com.example.social_network_gui.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage stage;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Repository<Long, User> userDatabaseRepository = new UserDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        Repository<Long, Event> eventDatabaseRepository = new EventsDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        Repository<Tuple<User, User>, Friendship> friendshipDatabaseRepository = new FriendshipDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        Repository<Tuple<User, User>, FriendRequest> friendRequestRepository = new RequestsDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        Repository<Long, Message> messageRepository = new MessageDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        Repository<Long, RoleType> roleTypeRepository = new RolesDatabaseRepository("jdbc:postgresql://localhost:5432/lab3_database", "postgres", "aygean");
        UserService userService = new UserService(userDatabaseRepository, new UserValidator());
        FriendshipService friendshipService = new FriendshipService(friendshipDatabaseRepository, new FriendshipValidator());
        NetworkService networkService = new NetworkService(friendshipDatabaseRepository, userDatabaseRepository, friendRequestRepository, messageRepository, roleTypeRepository);
        EventService eventService = new EventService(eventDatabaseRepository);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("controller/login-view.fxml"));

        AnchorPane root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setServices(userService, friendshipService, networkService,eventService);


        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Social Network");
        //primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stage.getScene().setRoot(pane);

    }

}
