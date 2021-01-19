package com.wangjin.doc.handler;

import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.base.TemplateExport;
import com.wangjin.doc.fifter.Filter;
import com.wangjin.doc.handler.impl.RequestInfoBaseFilterImpl;
import com.wangjin.doc.handler.impl.ResponseInfoBaseFilterImpl;
import com.wangjin.doc.util.BaseUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wangjin.doc.base.Constant.GSON;
import static com.wangjin.doc.util.BaseUtils.print;


/**
 * 解析工厂 大致功能为: 解析模板文件==>逐一解析InterfaceDoc==>输出文件
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ParseFactory {
    @Getter
    private final static ThreadLocal<InterfaceDoc> INTERFACE_DOC_THREAD_LOCAL = new ThreadLocal<>();
    @Getter
    private final static ThreadLocal<JsonObject> JSON_OBJECT_THREAD_LOCAL = ThreadLocal.withInitial(() ->
            JsonParser.parseString(TemplateExport.getTemplateExport()).getAsJsonObject());

    private final static List<Filter> FILTERS = new ArrayList<>();

    static {
        FILTERS.add(new RequestInfoBaseFilterImpl());
        FILTERS.add(new ResponseInfoBaseFilterImpl());
    }


    public ParseFactory(final InterfaceDoc interfaceDoc) {
        INTERFACE_DOC_THREAD_LOCAL.set(interfaceDoc);
    }

    @SneakyThrows
    public final void exec() {
        try {
            InterfaceDoc interfaceDoc = INTERFACE_DOC_THREAD_LOCAL.get();

            for (InterfaceDoc.MethodDoc selectText : Application.getSELECTED_TEXT()) {
                //这里对 RequestMapping 进行设置
                selectText.setRequestMapping(getReplaceRequestMapping(interfaceDoc, selectText));
            }
            for (InterfaceDoc.MethodDoc doc : interfaceDoc.getMethodDoc()) {
                //这里对 RequestMapping 进行设置
                doc.setRequestMapping(getReplaceRequestMapping(interfaceDoc, doc));
            }

            for (InterfaceDoc.MethodDoc doc : interfaceDoc.getMethodDoc()) {

                if (!Application.getSELECTED_TEXT().isEmpty()) {
                    if (Application.getSELECTED_TEXT().contains(doc)) {
                        print("选择了代码块,开始进行单独处理 :RequestMapping:{}", doc.getRequestMapping());
                    } else {
                        continue;
                    }
                }

                final JsonObject obj = JSON_OBJECT_THREAD_LOCAL.get();
                final JsonObject baseInfo = obj.getAsJsonObject("baseInfo");
                baseInfo.addProperty("apiName", doc.getComment());
                baseInfo.addProperty("apiURI", doc.getRequestMapping());
                baseInfo.addProperty("apiRequestType", doc.getMethodType().getApiRequestType());
                baseInfo.addProperty("apiUpdateTime", System.currentTimeMillis());
                FILTERS.forEach(filter -> filter.filter(doc));

                try {
                    LoginDocHandler.addQueue(GSON.toJson(Collections.singletonList(obj)), doc);
                } catch (Exception e) {
                    BaseUtils.printError("自动同步到接口文档系统出错 requestMapping:{}", interfaceDoc.getRequestMapping());
                }
            }
        } finally {
            clear();
        }
    }


    private void clear() {
        INTERFACE_DOC_THREAD_LOCAL.remove();
        JSON_OBJECT_THREAD_LOCAL.remove();
    }

    private String getReplaceRequestMapping(InterfaceDoc interfaceDoc, InterfaceDoc.MethodDoc doc) {
        return StrUtil.removeSuffix(interfaceDoc.getRequestMapping() + StrUtil.addPrefixIfNot(doc.getRequestMapping(), "/"), "/")
                .replace(":\\\\d+", "")
                .replace(":\\\\S+", "")
                .replace(":\\\\s+", "")
                .replace(":\\\\d", "")
                .replace(":\\\\S", "")
                .replace(":\\\\s", "");
    }
}
