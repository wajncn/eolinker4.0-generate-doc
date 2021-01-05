package com.wangjin.doc.handler.impl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.cache.FileCache;
import com.wangjin.doc.domain.RequestInfo;
import com.wangjin.doc.handler.ParseFilter;

import java.nio.file.Paths;

import static com.wangjin.doc.utils.BaseUtils.paramTypeFormat;

/**
 * 处理统一请求参数
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class RequestInfoParseFilterImpl extends ParseFilter {

    private static final String REQUEST_INFO = "requestInfo";

    @Override
    protected void filter(InterfaceDoc.MethodDoc doc) {
        JsonObject obj = getJSONObject();

        final JsonArray requestInfos = new JsonArray();
        obj.add(REQUEST_INFO,requestInfos);
        for (InterfaceDoc.Args requestArg : doc.getRequestArgs()) {
            this.filter(requestInfos, requestArg);
        }
    }

    private void filter(JsonArray requestInfos, InterfaceDoc.Args requestArg) {
        FileCache.FC fc = FileCache.getFc(requestArg.getType());
        if (fc == null) {
            //普通参数, 比如int,string等
            requestInfos.add(GSON.toJsonTree(RequestInfo.builder()
                    .paramType(paramTypeFormat(requestArg.getType()))
                    .paramKey(requestArg.getField())
                    .paramName(requestArg.getComment())
                    .build()));
            return;
        }

        // 实体对象
        CompilationUnit cu = PARSE_HANDLER.handler(Paths.get(fc.getFilePath()));
        TypeDeclaration<?> type = cu.getType(0);


        super.parseMember(requestInfos, type.getMembers(), null, true);
        super.parseExtend(requestInfos, type, null, true);
    }
}
