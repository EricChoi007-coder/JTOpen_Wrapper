package com.scl.as400wrapper.domain.rpgParams;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RpgParamAs400 {
    private Integer id;

    private String description;

    private RpgParamInOutType rpgParamInOutType;  // in-out type

    private RpgParamInSpecType rpgParamInSpecType; //specific type

    @JsonProperty("param_content")
    private Object paramContent;
    @JsonProperty("param_content_length")
    private Integer paramContentLength;

    @JsonProperty("decimal_range")  //format should be "20|1"
    private String decimalRange;


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

    public RpgParamInOutType getRpgParamInOutType() {
        return rpgParamInOutType;
    }

    public void setRpgParamInOutType(RpgParamInOutType rpgParamInOutType) {
        this.rpgParamInOutType = rpgParamInOutType;
    }

    public RpgParamInSpecType getRpgParamInSpecType() {
        return rpgParamInSpecType;
    }

    public void setRpgParamInSpecType(RpgParamInSpecType rpgParamInSpecType) {
        this.rpgParamInSpecType = rpgParamInSpecType;
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

    public String getDecimalRange() {
        return decimalRange;
    }

    public void setDecimalRange(String decimalRange) {
        this.decimalRange = decimalRange;
    }
}
