package com.wangjin.doc.base;

import cn.hutool.core.io.IoUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


/**
 * 主要是用于加载生成文档的模板, 只加载一次
 */
@NoArgsConstructor
public class TemplateExport {
    @Getter
    private static String templateExport;

    @Getter
    private static String docProperties;

    static {
        templateExport = IoUtil.read(Objects.requireNonNull(TemplateExport.class.getClassLoader()
                .getResourceAsStream(Constant.TEMPLATE_EXPORT_NAME)), "UTF-8");

        docProperties = IoUtil.read(Objects.requireNonNull(TemplateExport.class.getClassLoader()
                .getResourceAsStream(Constant.DOC_CONFIG_PROPERTIES_NAME)), "UTF-8");
    }
}
