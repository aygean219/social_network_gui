package com.example.social_network_gui.domain;

import com.example.social_network_gui.utils.Status;

public class UserRequestDTO {
    private User user;
    private Status status;

    private Tuple<User,User> idRequest;

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserRequestDTO(User user, Status status) {
        this.user = user;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public Status getStatus() {
        return status;
    }

    public Tuple<User,User> getIdRequest() {
        return idRequest;
    }
}
