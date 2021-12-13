package com.example.social_network_gui.controller;

import com.example.social_network_gui.controller.ShowFriendRequestController;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MenuMainController {
    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    public void initial(UserService userService,FriendshipService friendshipService,NetworkService networkService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
    }

    public void OpenShowFriendRequest(MouseEvent mouseEvent) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/social_network_gui/show-friend-request-view.fxml"));
        AnchorPane root=loader.load();

        Stage register_stage = new Stage();
        ShowFriendRequestController showFriendRequestController = loader.getController();
        showFriendRequestController.setService(userService,friendshipService,networkService);
        register_stage.setScene(new Scene(root));
        register_stage.show();

    }

}
