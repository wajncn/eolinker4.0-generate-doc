package com.wangjin.doc.base;

import com.google.gson.Gson;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class Constant {
    public static final String DOC_CONFIG_PROPERTIES_NAME = "doc.properties";
    public static final String TEMPLATE_EXPORT_NAME = "template.export";
    public static final Gson GSON = new Gson();

    public static final String NO_ANNOTATION_INTERFACE_TEXT = "无注释接口";
    public static final String NO_ANNOTATION_FIELD_TEXT = "";
    public static final String PAGE_QUERY_TEXT = "    (分页查询)";
}
