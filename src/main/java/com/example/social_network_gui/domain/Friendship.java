package com.example.social_network_gui.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<User, User>> {

    String date;

    public Friendship(Tuple<User, User> t, String date) {
        setId(t);
        setDate(date);
    }

    @Override
    public String toString() {
        return "First User{" + getId().getE1().getId() + "," + getId().getE1().getFirstName() + " " + getId().getE1().getLastName() + "} "
                + "Second User{" + getId().getE2().getId() + "," + getId().getE2().getFirstName() + " " + getId().getE2().getLastName() + "}" + " Date: " + date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getMonth() {
        if (this.date != null) {
            String[] dateParts = date.split("-");
            String month = dateParts[1];
            return month;
        }
        return "null";
    }
}
