package com.scl.as400wrapper.domain.spParams;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpParamAs400 {
    private Integer id;

    private String description;

    private SpParamInOutType spParamInOutType;  // in-out type

    private SpParamInSpecType spParamInSpecType; //specific type

    @JsonProperty("param_content")
    private Object paramContent;

    @JsonProperty("param_content_length")
    private Integer paramContentLength;

    public SpParamAs400(Integer id, String description, SpParamInOutType spParamInOutType, SpParamInSpecType spParamInSpecType, Object paramContent, Integer paramContentLength) {
        this.id = id;
        this.description = description;
        this.spParamInOutType = spParamInOutType;
        this.spParamInSpecType = spParamInSpecType;
        this.paramContent = paramContent;
        this.paramContentLength = paramContentLength;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SpParamInOutType getParamInOutType() {
        return spParamInOutType;
    }

    public void setParamInOutType(SpParamInOutType spParamInOutType) {
        this.spParamInOutType = spParamInOutType;
    }

    public SpParamInSpecType getParamInSpecType() {
        return spParamInSpecType;
    }

    public void setParamInSpecType(SpParamInSpecType spParamInSpecType) {
        this.spParamInSpecType = spParamInSpecType;
    }

    public Object getParamContent() {
        return paramContent;
    }

    public void setParamContent(Object paramContent) {
        this.paramContent = paramContent;
    }

    public Integer getParamContentLength() {
        return paramContentLength;
    }

    public void setParamContentLength(Integer paramContentLength) {
        this.paramContentLength = paramContentLength;
    }
}
