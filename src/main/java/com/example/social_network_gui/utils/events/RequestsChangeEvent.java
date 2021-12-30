package com.example.social_network_gui.utils.events;

import com.example.social_network_gui.domain.FriendRequest;

public class RequestsChangeEvent implements Event {
    private ChangeEventType type;
    private FriendRequest data, oldData;

    public RequestsChangeEvent(ChangeEventType type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }
    public RequestsChangeEvent(ChangeEventType type, FriendRequest data, FriendRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendRequest getData() {
        return data;
    }

    public FriendRequest getOldData() {
        return oldData;
    }
}
