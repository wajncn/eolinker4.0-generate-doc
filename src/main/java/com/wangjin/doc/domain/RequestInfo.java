package com.wangjin.doc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestInfo {

    @SerializedName("paramType")
    private final String paramType;

    @SerializedName("paramKey")
    private final String paramKey;

    @SerializedName("paramValueList")
    private final List<?> paramValueList = new ArrayList<>();

    @SerializedName("paramName")
    private final String paramName;


    @SerializedName("default")
    private final String defaultV = "";

    /**
     * 0就是必填  1就是不填
     */
    @SerializedName("paramNotNull")
    private final String paramNotNull;

    @SerializedName("$index")
    private final String index = "0";

    @SerializedName("paramNote")
    private final String paramNote = "";

    @SerializedName("paramLimit")
    private final String paramLimit = "";

    @SerializedName("paramValue")
    private final String paramValue;

}