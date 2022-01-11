package com.example.social_network_gui.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long>{
    private User from;
    private Chat to ;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public Message(User from,Chat to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = null;
    }

    public Message(User from, Chat to, String message, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public Chat getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply(){
        return reply;
    }
    @Override
    public String toString() {
        List<String> list_of_receiving = new ArrayList<>();
        for(User user: to.getUsers()){
            list_of_receiving.add(user.getFirstName()+" "+user.getLastName());
        }
        if(reply!=null) {
            String message = "ID:" + this.getId() + ";From:" + this.from.getFirstName() + " " + this.from.getLastName() + "TO: " + list_of_receiving + "Date:" + this.date + "Message: " + this.message + "ReplyId:" + this.reply.getId();
        }
        else{
            String message = "ID:" + this.getId() + ";From:" + this.from.getFirstName() + " " + this.from.getLastName() + "TO: " + list_of_receiving + "Date:" + this.date + "Message: " + this.message;
        }
        return message;
    }
}

