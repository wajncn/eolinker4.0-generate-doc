package com.wangjin.doc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultInfo {
    @SerializedName("paramKey")
    private final String paramKey;

    @SerializedName("paramType")
    private final String paramType;

    @SerializedName("paramValueList")
    private final List<?> paramValueList = new ArrayList<>();

    @SerializedName("paramNotNull")
    private final String paramNotNull = "0";

    @SerializedName("paramName")
    private final String paramName;

    @SerializedName("$index")
    private final String index = "0";
}
