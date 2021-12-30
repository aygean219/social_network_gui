package com.example.social_network_gui.domain;

import com.example.social_network_gui.domain.Tuple;
import com.example.social_network_gui.domain.User;
import com.example.social_network_gui.utils.Status;

public class RequestUserDTO {
    private String name;
    private Status status;

    private Tuple<User,User> idRequest;

    public RequestUserDTO(Tuple<User,User> idRequest,String name, Status status) {
        this.name = name;
        this.status = status;
        this.idRequest = idRequest;

    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Tuple<User,User> getIdRequest() {
        return idRequest;
    }
}