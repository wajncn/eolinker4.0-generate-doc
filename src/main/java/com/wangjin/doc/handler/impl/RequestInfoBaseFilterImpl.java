package com.wangjin.doc.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.cache.FileCache;
import com.wangjin.doc.domain.RequestInfo;
import com.wangjin.doc.handler.BaseFilter;
import com.wangjin.doc.util.BaseUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.wangjin.doc.base.Constant.GSON;
import static com.wangjin.doc.util.BaseUtils.paramTypeFormat;

/**
 * 处理统一请求参数
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class RequestInfoBaseFilterImpl extends BaseFilter {

    private static final String REQUEST_INFO = "requestInfo";
    /**
     * 忽略的请求值
     */
    private static final List<String> IGNORE_REQUEST = new ArrayList<String>(12) {{
        this.add("HttpServletRequest");
        this.add("HttpServletResponse");
    }};

    @Override
    public void filter(InterfaceDoc.MethodDoc doc) {
        JsonObject obj = super.getJsonObject();
        final JsonArray requestInfos = new JsonArray();
        obj.add(REQUEST_INFO, requestInfos);
        for (InterfaceDoc.Args requestArg : doc.getRequestArgs()) {
            this.filter(requestInfos, requestArg);
        }
    }

    private void filter(JsonArray requestInfos, InterfaceDoc.Args requestArg) {
        FileCache.FC fc = FileCache.getFc(requestArg.getType());
        if (fc == null) {
            if (IGNORE_REQUEST.contains(requestArg.getType())) {
                return;
            }
            //普通参数, 比如int,string等
            requestInfos.add(GSON.toJsonTree(RequestInfo.builder()
                    .paramType(paramTypeFormat(requestArg.getType()))
                    .paramKey(requestArg.getField())
                    .paramName(BaseUtils.reformatMethodComment(requestArg.getComment()))
                    .paramValue((requestArg.getComment().length() > 10
                            ? StrUtil.center("", 50, "　") + "\n" + BaseUtils.reformatMethodComment(requestArg.getComment(), 999) : ""))
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
