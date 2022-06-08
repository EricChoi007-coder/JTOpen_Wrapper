package com.scl.as400wrapper.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SqlRequest {

    private String id;

    private String description;

    @JsonProperty("sql_content")
    private String sqlContent;

    //Db2Client Registry Info
    @JsonProperty("db2_url")
    private String db2Url;
    @JsonProperty("db2_username")
    private String db2Name;
    @JsonProperty("db2_password")
    private String db2Password;

    public SqlRequest() {

    }

    public SqlRequest(String id, String description, String sqlContent) {
        this.id = id;
        this.description = description;
        this.sqlContent = sqlContent;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public SqlRequest(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getDb2Url() {
        return db2Url;
    }

    public void setDb2Url(String db2Url) {
        this.db2Url = db2Url;
    }

    public String getDb2Name() {
        return db2Name;
    }

    public void setDb2Name(String db2Name) {
        this.db2Name = db2Name;
    }

    public String getDb2Password() {
        return db2Password;
    }

    public void setDb2Password(String db2Password) {
        this.db2Password = db2Password;
    }
}
