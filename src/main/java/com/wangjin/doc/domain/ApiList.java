package com.wangjin.doc.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Objects;


/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiList {

    /**
     * apiRequestType : 2
     * apiName : 用户门店信息、门店名称、昵称-头像修改
     * apiURI : /user
     * starred : 0
     * updateUserID : 130
     * apiUpdateTime : 2020-09-12 11:19:10
     * userNickName : 王进
     * userName : wangjin
     * apiID : 15187
     * apiStatus : 0
     */
    @SerializedName("apiRequestType")
    private int apiRequestType;


    @SerializedName("apiName")
    private String apiName;


    @SerializedName("apiURI")
    private String apiURI;


    @SerializedName("starred")
    private int starred;


    @SerializedName("updateUserID")
    private int updateUserID;

    @SerializedName("apiUpdateTime")
    private String apiUpdateTime;

    @SerializedName("userNickName")
    private String userNickName;

    @SerializedName("userName")
    private String userName;

    @SerializedName("apiID")
    private String apiID;

    @SerializedName("apiStatus")
    private int apiStatus;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;

        }
        if (o == null || getClass() != o.getClass()) {
            return false;

        }
        ApiList apiList = (ApiList) o;

        return Objects.equals(apiName, apiList.apiName) &&
                Objects.equals(apiURI, apiList.apiURI);

    }

    @Override
    public int hashCode() {
        return Objects.hash(apiName, apiURI);

    }

    public ApiList() {

    }

    public ApiList(String apiName, String apiURI) {
        this.apiName = apiName;

        this.apiURI = apiURI;

    }
}
