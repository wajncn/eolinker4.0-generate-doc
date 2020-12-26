package com.wangjin.doc.domain;

import cn.hutool.core.annotation.Alias;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class ResultInfo {
    private final String paramKey;
    private final String paramType;
    private final List<?> paramValueList = new ArrayList<>();
    private final String paramNotNull = "0";
    private final String paramName;

    @Alias("$index")
    private final String index = "0";
}
