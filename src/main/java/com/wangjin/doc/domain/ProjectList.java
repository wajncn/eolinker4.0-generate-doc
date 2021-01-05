package com.wangjin.doc.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;


/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectList {


    /**
     * projectType : 0
     * userType : 3
     * projectName : 亿品
     * projectID : 299
     * projectUpdateTime : 2020-05-11 13:59:59
     * projectVersion : 1.0
     */

    @SerializedName("projectType")
    private int projectType;

    @SerializedName("userType")
    private int userType;

    @SerializedName("projectName")
    private String projectName;

    @SerializedName("projectID")
    private int projectID;

    @SerializedName("projectUpdateTime")
    private String projectUpdateTime;

    @SerializedName("projectVersion")
    private String projectVersion;


}
