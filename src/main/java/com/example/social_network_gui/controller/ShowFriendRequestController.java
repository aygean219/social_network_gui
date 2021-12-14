package com.example.social_network_gui.controller;


import com.example.social_network_gui.domain.*;
import com.example.social_network_gui.service.FriendshipService;
import com.example.social_network_gui.service.NetworkService;
import com.example.social_network_gui.service.UserService;
import com.example.social_network_gui.utils.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ShowFriendRequestController {
    private ObservableList<RequestUserDTO> observableList = FXCollections.observableArrayList();
    @FXML
    public TableColumn<RequestUserDTO, Status> tableColumnStatus ;
    @FXML
    public TableColumn<RequestUserDTO,String> tableColumnName ;
    @FXML
    public TableView<RequestUserDTO> tableViewRequests ;
    @FXML
    private TextArea textArea;

    @FXML
    public void initialize(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<RequestUserDTO,String>("name"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<RequestUserDTO,Status>("status"));
        tableViewRequests.setItems(observableList);
    }

    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;

    public void setService(UserService userService,FriendshipService friendshipService,NetworkService networkService){
      this.userService = userService;
      this.friendshipService = friendshipService;
      this.networkService = networkService;
      observableList.setAll(requestUserDTOS());
    }

    private ArrayList<RequestUserDTO> requestUserDTOS(){
        Iterable<FriendRequest> friendships = networkService.friendshipIterable();
        List<FriendRequest> friendshipRequests = new ArrayList<FriendRequest>();

        friendshipRequests = StreamSupport.stream(friendships.spliterator(),false)
                .filter(x->x.getId().getE2().getId().equals(networkService.getLoggedUser().getId()))
                .collect(Collectors.toList());
        ArrayList<Tuple<User,FriendRequest>> list = new ArrayList<Tuple<User,FriendRequest>>();
        friendshipRequests.forEach(x->{
            list.add(new Tuple<>(userService.getUser(x.getId().getE1().getId()),x));
        });
        ArrayList<RequestUserDTO> requestListOfUserDTOS = new ArrayList<RequestUserDTO>();
        for(Tuple<User,FriendRequest> li: list){
            String fullName = li.getE1().getFirstName() + " " +li.getE1().getLastName();
            RequestUserDTO requestUserDTO = new RequestUserDTO(li.getE2().getId(),fullName,li.getE2().getStatus());
            requestListOfUserDTOS.add(requestUserDTO);
        }
        System.out.println("-----------0----------- ");
        return requestListOfUserDTOS;
    }

    public void accept_request(MouseEvent mouseEvent){

        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if(selected != null){
            if(selected.getStatus().equals(Status.PENDING)){
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.acceptFriendRequest(request.getId().getE1().getId().toString());
            }
        }
        observableList.setAll(requestUserDTOS());
    }

    public void reject_request(MouseEvent mouseEvent){
        RequestUserDTO selected = (RequestUserDTO) tableViewRequests.getSelectionModel().getSelectedItem();
        if(selected != null){
            if(selected.getStatus().equals(Status.PENDING)){
                FriendRequest request = networkService.getRequest(selected.getIdRequest());
                networkService.rejectFriendRequest(request.getId().getE1().getId().toString());
            }
        }
        observableList.setAll(requestUserDTOS());
    }
}
