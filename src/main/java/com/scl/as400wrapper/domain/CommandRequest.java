package com.scl.as400wrapper.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommandRequest {

    private String description;

    @JsonProperty("cmd_content")
    private String cmdContent;

    //AS400 Client Registry Info
    private String serverUrl;
    private String userName;
    private String userPassword;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCmdContent() {
        return cmdContent;
    }

    public void setCmdContent(String cmdContent) {
        this.cmdContent = cmdContent;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
