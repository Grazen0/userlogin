package com.elchologamer.userlogin.api.types;

public enum AuthType {

    LOGIN("logged_in"),
    REGISTER("registered");

    private final String messageKey;

    AuthType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}