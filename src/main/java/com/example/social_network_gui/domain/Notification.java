package com.example.social_network_gui.domain;

import java.time.LocalDateTime;

public class Notification extends Entity<Long>{
    private User id_user;
    private Event notification_info;
    private String remaining_time;

    public Notification(User id_user, Event notification_info, String remaining_time) {
        this.id_user = id_user;
        this.notification_info = notification_info;
        this.remaining_time = remaining_time;
    }

    public User getId_user() {
        return id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }

    public Event getNotification_info() {
        return notification_info;
    }

    public void setNotification_info(Event notification_info) {
        this.notification_info = notification_info;
    }

    public String getRemaining_time() {
        return remaining_time;
    }

    public void setRemaining_time(String remaining_time) {
        this.remaining_time = remaining_time;
    }
}
