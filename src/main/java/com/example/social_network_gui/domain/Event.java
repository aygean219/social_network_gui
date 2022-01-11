package com.example.social_network_gui.domain;

import com.example.social_network_gui.utils.EventSubscription;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Event extends Entity<Long> {
    private String title;
    private String description;
    private String location;
    private LocalDateTime dateTime;
    private Map<User, EventSubscription> users;

    public Event(String title, String description, String location, LocalDateTime dateTime) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.users = new HashMap<>();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<User, EventSubscription> getUsers() {
        return users;
    }

    public void setUsers(Map<User, EventSubscription> users) {
        this.users = users;
    }

    public void addUser(User user, EventSubscription eventSubscription) {
        this.users.put(user, eventSubscription);
    }

    public void subscribeUser(User user) {
        this.users.remove(user);
        this.users.put(user, EventSubscription.SUBSCRIBE);
    }

    public void unsubscribeUser(User user) {
        this.users.remove(user);
        this.users.put(user, EventSubscription.UNSUBSCRIBE);
    }


    @Override
    public String toString() {
        return title.toUpperCase() + "\n  " + description +
                "\n  " + location + "\n  " + dateTime.getDayOfMonth() +
                " " + dateTime.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH)
                + " " + dateTime.format(DateTimeFormatter.ofPattern("yyyy, HH:mm"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(this.getId(), event.getId());
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
