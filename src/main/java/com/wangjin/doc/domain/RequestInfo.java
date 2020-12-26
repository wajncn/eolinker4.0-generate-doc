package com.wangjin.doc.domain;

import cn.hutool.core.annotation.Alias;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class RequestInfo {

    private final String paramType;
    private final String paramKey;
    private final List<?> paramValueList = new ArrayList<>();
    private final String paramName;


    @Alias("default")
    private final String defaultV = "";

    private final String paramNotNull = "0";
    @Alias("$index")
    private final String index = "0";

    private final String paramNote = "";
    private final String paramLimit = "";
    private final String paramValue = "";

}