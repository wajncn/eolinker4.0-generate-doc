package com.wangjin.doc.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import com.wangjin.doc.handler.impl.RequestInfoParseFilterImpl;
import com.wangjin.doc.handler.impl.ResponseInfoParseFilterImpl;
import com.wangjin.doc.util.BaseUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.wangjin.doc.base.Application.BASE_PATH;
import static com.wangjin.doc.util.BaseUtils.print;


/**
 * 解析工厂 大致功能为: 解析模板文件==>逐一解析InterfaceDoc==>输出文件
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ParseFactory {

    protected final static ParseHandler<CompilationUnit> PARSE_HANDLER = new JavaParseHandlerImpl();
    private final static ThreadLocal<InterfaceDoc> THREAD_LOCAL = new ThreadLocal<>();
    private final static ThreadLocal<JsonObject> JSON_OBJECT = new ThreadLocal<>();
    private final static List<ParseFilter> FILTERS = new ArrayList<ParseFilter>() {{
        this.add(new RequestInfoParseFilterImpl());
        this.add(new ResponseInfoParseFilterImpl());
    }};

    protected final static Gson GSON = new Gson();

    public ParseFactory(final InterfaceDoc interfaceDoc) {
//        if (JSON_OBJECT.get() != null) {
//            return;
//        }
        String jsonstr = IoUtil.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("template.export")), "UTF-8");
        JSON_OBJECT.set(JsonParser.parseString(jsonstr).getAsJsonObject());
        THREAD_LOCAL.set(interfaceDoc);
    }

    protected static InterfaceDoc getInterfaceDoc() {
        return THREAD_LOCAL.get();
    }

    protected static JsonObject getJSONObject() {
        return JSON_OBJECT.get();
    }

    @SneakyThrows
    public final void exec() {

        DocConfig docConfig = DocConfig.get();


        InterfaceDoc interfaceDoc = getInterfaceDoc();

        List<JsonObject> array = new ArrayList<>(24);
        String name = StrUtil.removeSuffix(Paths.get(interfaceDoc.getFilePath()).getFileName().toString(), ".java");

        String detailPath = BASE_PATH + File.separator + "detail" + File.separator + name;
        if (docConfig == null || !docConfig.isSynchronous()) {
            FileUtil.mkdir(detailPath);
        }

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

            final JsonObject obj = getJSONObject();
            final JsonObject baseInfo = obj.getAsJsonObject("baseInfo");
            baseInfo.addProperty("apiName", doc.getComment());
            baseInfo.addProperty("apiURI", doc.getRequestMapping());
            baseInfo.addProperty("apiRequestType", doc.getMethodType().getApiRequestType());
            baseInfo.addProperty("apiUpdateTime", System.currentTimeMillis());
            FILTERS.forEach(filter -> filter.filter(doc));

            if (docConfig == null || !docConfig.isSynchronous()) {
                String filename = BaseUtils.replaceIllegalityStr(StrUtil.blankToDefault(doc.getComment(), doc.getRequestMapping()));
                print("{} =======> 文档已生成完毕", StrUtil.padAfter(filename, 30, " "));
                IoUtil.writeUtf8(new FileOutputStream(detailPath + File.separator + filename + ".json"),
                        true,
                        GSON.toJson(Collections.singletonList(obj)));
            }

            array.add(obj);
            try {
                LoginDocHandler.addQueue(GSON.toJson(Collections.singletonList(obj)), doc);
            } catch (Exception e) {
                BaseUtils.printError("自动同步到接口文档系统出错 requestMapping:{}", interfaceDoc.getRequestMapping());
            }
        }
        LoginDocHandler.startUpload();
        if (docConfig == null || !docConfig.isSynchronous()) {
            IoUtil.writeUtf8(new FileOutputStream(BASE_PATH + File.separator + StrUtil.removeSuffix(name, ".java") + ".json"), true, GSON.toJson(array));
        }
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
