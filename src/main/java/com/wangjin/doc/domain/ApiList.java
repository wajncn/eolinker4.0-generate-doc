package com.wangjin.doc.domain;


import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.Objects;


/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
@Data
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
    @Alias("apiRequestType")
    private int apiRequestType;


    @Alias("apiName")
    private String apiName;


    @Alias("apiURI")
    private String apiURI;


    @Alias("starred")
    private int starred;


    @Alias("updateUserID")
    private int updateUserID;

    @Alias("apiUpdateTime")
    private String apiUpdateTime;

    @Alias("userNickName")
    private String userNickName;

    @Alias("userName")
    private String userName;

    @Alias("apiID")
    private String apiID;

    @Alias("apiStatus")
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
