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
import com.wangjin.doc.handler.BaseFilter;
import com.wangjin.doc.util.BaseUtils;
import lombok.AllArgsConstructor;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.wangjin.doc.base.Constant.GSON;
import static com.wangjin.doc.util.BaseUtils.*;

/**
 * 统一处理返回值
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ResponseInfoBaseFilterImpl extends BaseFilter {

    private static final String RESULT_INFO = "resultInfo";
    private static final String VOID = "void";
    /**
     * 忽略的返回值
     */
    private static final Set<String> IGNORE_RESULT = new HashSet<String>(12) {{
        this.add("RequestResult");
        this.add("JSONResult");
    }};
    private static final ThreadLocal<Page_Flag> PAGE_FLAG = new ThreadLocal<>();

    public static void addIGNORE_RESULT(String v) {
        if (StrUtil.isEmpty(v) || ",".equals(v)) {
            return;
        }
        IGNORE_RESULT.add(v);
    }

    @Override
    public void filter(InterfaceDoc.MethodDoc doc) {
        PAGE_FLAG.remove();

        JsonObject obj = super.getJsonObject();

        final JsonArray resultInfos = new JsonArray();
        obj.add(RESULT_INFO, resultInfos);


        final String responseObject = formatResponseObject(doc);
        if (VOID.equals(responseObject)) {
            return;
        }

        FileCache.FC fc = FileCache.getFc(responseObject);

        if (fc == null) {
            //没有查到 说明返回类型不是实体对象
            resultInfos.add(GSON.toJsonTree(ResultInfo.builder().paramKey(responseObject)
                    .paramName(responseObject)
                    .paramType(paramTypeFormat(responseObject))
                    .build()));
            return;
        }
        Optional.ofNullable(PAGE_FLAG.get()).ifPresent(a -> a.handler(resultInfos));

        CompilationUnit cu = PARSE_HANDLER.handler(Paths.get(fc.getFilePath()));
        TypeDeclaration<?> type = cu.getType(0);

        super.parseMember(resultInfos, type.getMembers(), PAGE_FLAG.get() == null ? null : PAGE_FLAG.get().listKey, false);
        super.parseExtend(resultInfos, type, null, false);
    }

    /**
     * 格式化接口的返回值
     *
     * @param doc
     * @return
     */
    private String formatResponseObject(InterfaceDoc.MethodDoc doc) {
        String responseObject = doc.getResponseObject();

        //对新框架做处理
        if ("RequestResult".equals(responseObject)) {
            return VOID;
        }

        for (String ignore : IGNORE_RESULT) {
            if (responseObject.startsWith(ignore + "<")) {
                responseObject = responseObject.replace(ignore + "<", "");
                responseObject = StrUtil.removeSuffix(responseObject, ">");
            }
        }

        if (responseObject.startsWith("PageData<")) {
            //没有加泛型 开始容错处理:
            responseObject = Optional.ofNullable(ReUtil.findAll("PageData<(\\S+),(\\S+)>", responseObject, 1))
                    .map(a -> a.get(0)).orElse(VOID);
            PAGE_FLAG.set(Page_Flag.PAGE_2_0);
            return responseObject;
        }


        if ("PageInfo".equals(responseObject)) {
            //没有加泛型 开始容错处理:
            responseObject = this.parseResponseObject(doc);

            PAGE_FLAG.set(Page_Flag.PAGE_1_0);
        }

        if (responseObject.startsWith("List<")) {
            responseObject = responseObject.replace("List<", "");
            responseObject = responseObject.replace(">", "");
        }

        if (responseObject.startsWith("PageInfo<")) {
            responseObject = responseObject.replace("PageInfo<", "");
            responseObject = responseObject.replace(">", "");
            PAGE_FLAG.set(Page_Flag.PAGE_1_0);
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


    @AllArgsConstructor
    public enum Page_Flag {
        //老框架的分页
        PAGE_1_0("list>>") {
            @Override
            void handler(JsonArray array) {
                BaseUtils.getPAGE_INFO().forEach(array::add);
            }
        },

        //新框架的分页
        PAGE_2_0("list>>") {
            @Override
            void handler(JsonArray array) {
                BaseUtils.getPAGE_INFO_new_framework().forEach(array::add);
            }
        };

        public final String listKey;

        abstract void handler(JsonArray array);
    }

}
