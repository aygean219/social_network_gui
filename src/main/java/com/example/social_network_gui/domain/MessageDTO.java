package com.example.social_network_gui.domain;

public class MessageDTO {
    private Long idMessage;
    private String sender;
    private String message;

    public MessageDTO(Long idMessage, String sender, String message) {
        this.idMessage = idMessage;
        this.sender = sender;
        this.message = message;
    }

    @Override
    public String toString() {
        return "idMessage=" + idMessage +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
