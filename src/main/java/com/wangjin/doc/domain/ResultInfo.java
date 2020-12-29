package com.wangjin.doc.domain;

import cn.hutool.core.annotation.Alias;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Builder;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class ResultInfo {
    @Alias("paramKey")
    private final String paramKey;

    @Alias("paramType")
    private final String paramType;

    @Alias("paramValueList")
    private final List<?> paramValueList = new ArrayList<>();

    @Alias("paramNotNull")
    private final String paramNotNull = "0";

    @Alias("paramName")
    private final String paramName;

    @Alias("$index")
    private final String index = "0";
}
