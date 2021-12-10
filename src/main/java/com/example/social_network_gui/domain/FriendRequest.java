package com.example.social_network_gui.domain;

import com.example.social_network_gui.utils.Status;


public class FriendRequest extends Entity<Tuple<User, User>> {

    private Status status;

    public FriendRequest(Tuple<User, User> t, Status status) {
        setId(t);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "From " + getId().getE1().getFirstName() + " " + getId().getE1().getLastName() + "(" + getId().getE1().getId() + "), " +
                "To " + getId().getE2().getFirstName() + " " + getId().getE2().getLastName() + "(" + getId().getE2().getId() + "), " +
                "status: " + status;
    }


}
