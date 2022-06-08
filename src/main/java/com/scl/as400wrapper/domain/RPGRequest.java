package com.scl.as400wrapper.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scl.as400wrapper.domain.rpgParams.RpgParamAs400;

import java.util.List;

public class RPGRequest {

    private String id;

    private String description;

    @JsonProperty("rpg_destination")
    private String rpgDestination;

//    @JsonProperty("rpg_content")
//    private String rpgContent;

    @JsonProperty("cmd_content")
    private String cmdContent;

    //AS400 Client Registry Info
    private String rpgUrl;
    private String userName;
    private String userPassword;

    private List<RpgParamAs400> rpgParamAs400List;



    public List<RpgParamAs400> getRpgParamAs400List() {
        return rpgParamAs400List;
    }

    public void setRpgParamAs400List(List<RpgParamAs400> rpgParamAs400List) {
        this.rpgParamAs400List = rpgParamAs400List;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRpgDestination() {
        return rpgDestination;
    }

    public void setRpgDestination(String rpgDestination) {
        this.rpgDestination = rpgDestination;
    }

//    public String getRpgContent() {
//        return rpgContent;
//    }
//
//    public void setRpgContent(String rpgContent) {
//        this.rpgContent = rpgContent;
//    }

    public String getRpgUrl() {
        return rpgUrl;
    }

    public void setRpgUrl(String rpgUrl) {
        this.rpgUrl = rpgUrl;
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

    public String getCmdContent() {
        return cmdContent;
    }

    public void setCmdContent(String cmdContent) {
        this.cmdContent = cmdContent;
    }
}
