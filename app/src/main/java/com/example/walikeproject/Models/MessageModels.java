package com.example.walikeproject.Models;

public class MessageModels {
    String UId, messageText, messageId;
    Long timestamp;

    public MessageModels(String UId, String messageText, Long timestamp) {
        this.UId = UId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public MessageModels(String UId, String messageText) {
        this.UId = UId;
        this.messageText = messageText;
    }

    public MessageModels() {

    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
