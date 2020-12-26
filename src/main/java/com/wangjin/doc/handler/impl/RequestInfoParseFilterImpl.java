package com.wangjin.doc.handler.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
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

    @Override
    protected void filter(InterfaceDoc.MethodDoc doc) {
        JSONObject obj = getJSONObject();

        final JSONArray requestInfos = obj.getJSONArray("requestInfo");
        requestInfos.clear();

        for (InterfaceDoc.Args requestArg : doc.getRequestArgs()) {
            this.filter(requestInfos, requestArg);
        }
    }

    private void filter(JSONArray requestInfos, InterfaceDoc.Args requestArg) {
        FileCache.FC fc = FileCache.getFc(requestArg.getType());
        if (fc == null) {
            //普通参数, 比如int,string等
            requestInfos.add(RequestInfo.builder()
                    .paramType(paramTypeFormat(requestArg.getType()))
                    .paramKey(requestArg.getField())
                    .paramName(requestArg.getComment())
                    .build());
            return;
        }

        // 实体对象
        CompilationUnit cu = parseHandler.handler(Paths.get(fc.getFilePath()));
        TypeDeclaration<?> type = cu.getType(0);


        super.parseMember(requestInfos, type.getMembers(), null, true);
        super.parseExtend(requestInfos, type, null, true);
    }
}
