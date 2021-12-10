package com.example.social_network_gui.domain;

public class FriendshipDTO {
    private String firstName;
    private String lastName;
    private String date;

    public FriendshipDTO(String firstName, String lastName, String date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    @Override
    public String toString() {
        return firstName + "|" + lastName + "|" + date;
    }
}
