package com.wangjin.doc.domain;

import cn.hutool.core.annotation.Alias;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class RequestInfo {

    @Alias("paramType")
    private final String paramType;

    @Alias("paramKey")
    private final String paramKey;

    @Alias("paramValueList")
    private final List<?> paramValueList = new ArrayList<>();

    @Alias("paramName")
    private final String paramName;


    @Alias("default")
    private final String defaultV = "";

    @Alias("paramNotNull")
    private final String paramNotNull = "0";

    @Alias("$index")
    private final String index = "0";

    @Alias("paramNote")
    private final String paramNote = "";

    @Alias("paramLimit")
    private final String paramLimit = "";

    @Alias("paramValue")
    private final String paramValue = "";

}