package com.wangjin.doc.handler.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.cache.FileCache;
import com.wangjin.doc.domain.ResultInfo;
import com.wangjin.doc.handler.ParseFilter;

import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wangjin.doc.utils.BaseUtils.*;

/**
 * 统一处理返回值
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ResponseInfoParseFilterImpl extends ParseFilter {

    private static final String RESULT_INFO = "resultInfo";

    @Override
    protected void filter(InterfaceDoc.MethodDoc doc) {
        JsonObject obj = getJSONObject();
        final JsonArray resultInfos = new JsonArray();
        obj.add(RESULT_INFO, resultInfos);


        if ("void".equals(doc.getResponseObject())) {
            return;
        }


        AtomicBoolean isPage = new AtomicBoolean(false);

        final String responseObject = formatResponseObject(doc, isPage);

        FileCache.FC fc = FileCache.getFc(responseObject);

        if (fc == null) {
            //没有查到 说明返回类型不是实体对象
            resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey(responseObject)
                    .paramName(responseObject)
                    .paramType(paramTypeFormat(responseObject))
                    .build()));
            return;
        }

        //直接在缓存中命中, 则说明当前返回值是某个类.  开始解析
        if (isPage.get()) {
            addPage(resultInfos);
        }
        CompilationUnit cu = PARSE_HANDLER.handler(Paths.get(fc.getFilePath()));
        TypeDeclaration<?> type = cu.getType(0);

        super.parseMember(resultInfos, type.getMembers(), isPage.get() ? "list>>" : null, false);
        super.parseExtend(resultInfos, type, null, false);
    }


    private void addPage(JsonArray resultInfos) {
        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageNum")
                .paramName("当前页")
                .paramType(paramTypeFormat("int"))
                .build()));

        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageSize")
                .paramName("每页的数量")
                .paramType(paramTypeFormat("int"))
                .build()));

        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("size")
                .paramName("当前页的数量")
                .paramType(paramTypeFormat("int"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pages")
                .paramName("总页数")
                .paramType(paramTypeFormat("int"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("prePage")
                .paramName("前一页")
                .paramType(paramTypeFormat("int"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("nextPage")
                .paramName("下一页")
                .paramType(paramTypeFormat("int"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("isFirstPage")
                .paramName("是否为第一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("isLastPage")
                .paramName("是否为最后一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("hasPreviousPage")
                .paramName("是否有前一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("hasNextPage")
                .paramName("是否有下一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("total")
                .paramName("总记录数")
                .paramType(paramTypeFormat("int"))
                .build()));


        resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey("list")
                .paramName("结果集")
                .paramType(paramTypeFormat("List"))
                .build()));
    }


    /**
     * 格式化接口的返回值
     *
     * @param doc
     * @return
     */
    private String formatResponseObject(InterfaceDoc.MethodDoc doc, AtomicBoolean isPage) {
        String responseObject = doc.getResponseObject();
        if ("PageInfo".equals(responseObject)) {
            //没有加泛型 开始容错处理:
            responseObject = this.parseResponseObject(doc);
            isPage.set(true);
        }

        if (responseObject.startsWith("List<")) {
            responseObject = responseObject.replace("List<", "");
            responseObject = responseObject.replace(">", "");
        }

        if (responseObject.startsWith("PageInfo<")) {
            responseObject = responseObject.replace("PageInfo<", "");
            responseObject = responseObject.replace(">", "");
            isPage.set(true);
        }
        return responseObject;
    }


    /**
     * 解析返回值,因为部分开发的接口没加泛型,这个方法的作用就是尽可能的推断出返回值
     *
     * @param doc
     * @return
     */
    private String parseResponseObject(InterfaceDoc.MethodDoc doc) {
        String responseObject = doc.getResponseObject();
        if (doc.getBody().isEmpty()) {
            return responseObject;
        }
        try {
            Statement statement = doc.getBody().get(doc.getBody().size() - 1);
            String body = statement.toString();
            String s = ReUtil.get("PageInfo<\\S+>", body, 0);
            if (s != null) {
                return s;
            }

            String service = ReUtil.get("\\((\\S+Service)", body, 1);
            if (service == null) {
                printError("分页返回值泛型推断失败,解析失败.   RequestMapping:{}", doc.getRequestMapping());
                return responseObject;
            }
            service = StrUtil.upperFirst(service);
            CompilationUnit handler = PARSE_HANDLER.handler(Paths.get(FileCache.getFc(service).getFilePath()));
            TypeDeclaration<?> type = handler.getType(0);
            if (type instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) type;
                return cid.getExtendedTypes().get(0).getTypeArguments().get().getFirst().get().asString();
            }
        } catch (Exception e) {
            printWarn("可忽略的异常");
        }
        return responseObject;
    }

}
