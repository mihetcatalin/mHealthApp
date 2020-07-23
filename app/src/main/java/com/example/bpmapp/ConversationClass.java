package com.example.bpmapp;

public class ConversationClass {
    private String message;
    private String sender;
    private String toFrom;
    private String type;

    public ConversationClass() {
        //empty constructor needed
    }

    public ConversationClass(String message, String sender, String toFrom, String type) {
        this.message = message;
        this.sender = sender;
        this.toFrom = toFrom;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getToFrom() {
        return toFrom;
    }

    public String getType() {
        return type;
    }

}
