package com.example.social_network_gui.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Chat extends Entity<Long>{
    private String name;
    private List<User> users ;
    private LocalDateTime date;

    public Chat(String name,List<User> to){
        this.name=name;
        this.users=to;
        this.date=LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(name, chat.name) && Objects.equals(users, chat.users) && Objects.equals(date, chat.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, users, date);
    }

    @Override
    public String toString() {
        return name ;
    }
}
