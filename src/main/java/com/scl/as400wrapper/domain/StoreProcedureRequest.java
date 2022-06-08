package com.scl.as400wrapper.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scl.as400wrapper.domain.spParams.SpParamAs400;

import java.util.List;

public class StoreProcedureRequest {

    private String id;

    private String description;

    @JsonProperty("sp_content")
    private String spContent;

    //AS400 Client Registry Info
    private String spUrl;
    private String userName;
    private String userPassword;

    private List<SpParamAs400> spParamAs400List;

    public StoreProcedureRequest(String id, String description, String spContent, String spUrl, String userName, String userPassword, List<SpParamAs400> spParamAs400List) {
        this.id = id;
        this.description = description;
        this.spContent = spContent;
        this.spUrl = spUrl;
        this.userName = userName;
        this.userPassword = userPassword;
        this.spParamAs400List = spParamAs400List;
    }

    public List<SpParamAs400> getParamAs400List() {
        return spParamAs400List;
    }

    public void setParamAs400List(List<SpParamAs400> spParamAs400List) {
        this.spParamAs400List = spParamAs400List;
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

    public String getSpContent() {
        return spContent;
    }

    public void setSpContent(String spContent) {
        this.spContent = spContent;
    }

    public String getSpUrl() {
        return spUrl;
    }

    public void setSpUrl(String spUrl) {
        this.spUrl = spUrl;
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
