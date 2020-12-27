package com.wangjin.doc.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import com.wangjin.doc.utils.BaseUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wangjin.doc.base.Main.BASE_PATH;
import static com.wangjin.doc.utils.BaseUtils.print;


/**
 * 解析工厂 大致功能为: 解析模板文件==>逐一解析InterfaceDoc==>输出文件
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ParseFactory {

    protected static final ParseHandler<CompilationUnit> parseHandler = new JavaParseHandlerImpl();
    private final static ThreadLocal<InterfaceDoc> THREAD_LOCAL = new ThreadLocal<>();
    private final static ThreadLocal<JSONObject> JSON_OBJECT = new ThreadLocal<>();
    private final List<ParseFilter> filters = new ArrayList<ParseFilter>();

    public ParseFactory(final InterfaceDoc interfaceDoc) {
        String jsonstr = IoUtil.read(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("template.export")), "UTF-8");
        JSONObject obj = JSONUtil.parseObj(jsonstr);
        JSON_OBJECT.set(obj);
        THREAD_LOCAL.set(interfaceDoc);
    }

    protected static InterfaceDoc getInterfaceDoc() {
        return THREAD_LOCAL.get();
    }

    protected static JSONObject getJSONObject() {
        return JSON_OBJECT.get();
    }

    public ParseFactory add(ParseFilter filter) {
        filters.add(filter);
        return this;
    }

    @SneakyThrows
    public final void exec() {

        DocConfig docConfig = DocConfig.get();


        InterfaceDoc interfaceDoc = getInterfaceDoc();

        JSONArray array = new JSONArray();
        String name = StrUtil.removeSuffix(Paths.get(interfaceDoc.getFilePath()).getFileName().toString(), ".java");

        String detailPath = BASE_PATH + File.separator + "detail" + File.separator + name;
        if (!docConfig.isSynchronous()) {
            FileUtil.mkdir(detailPath);
        }

        for (InterfaceDoc.MethodDoc doc : interfaceDoc.getMethodDoc()) {

            doc.setRequestMapping(
                    StrUtil.removeSuffix(interfaceDoc.getRequestMapping() + StrUtil.addPrefixIfNot(doc.getRequestMapping(), "/"), "/")
                            .replace(":\\\\d+", "")
                            .replace(":\\\\S+", "")
                            .replace(":\\\\s+", "")
                            .replace(":\\\\d", "")
                            .replace(":\\\\S", "")
                            .replace(":\\\\s", "")
            );

            JSONObject obj = getJSONObject();
            JSONObject baseInfo = obj.getJSONObject("baseInfo");
            baseInfo.set("apiName", doc.getComment());
            baseInfo.set("apiURI", doc.getRequestMapping());
            baseInfo.set("apiRequestType", doc.getMethodType().getApiRequestType());
            baseInfo.set("apiUpdateTime",System.currentTimeMillis());
            filters.forEach(filter -> filter.filter(doc));

            final JSONObject docJSON = JSON_OBJECT.get();


            if (!docConfig.isSynchronous()) {
                String filename = BaseUtils.replaceIllegalityStr(StrUtil.blankToDefault(doc.getComment(), doc.getRequestMapping()));
                print("{} =======> 文档已生成完毕", StrUtil.padAfter(filename, 30, " "));
                IoUtil.writeUtf8(new FileOutputStream(detailPath + File.separator + filename + ".json"), true, new JSONArray() {{
                    this.add(docJSON);
                }});
            }

            array.add(JSONUtil.parseObj(docJSON.toString()));
            try {
                JSONArray upload = new JSONArray();
                upload.add(JSONUtil.parseObj(docJSON.toString()));

                LoginDocHandler.upload(upload.toString(), doc);
            } catch (Exception e) {
                BaseUtils.printError("自动同步到接口文档系统出错 requestMapping:{}", interfaceDoc.getRequestMapping());
            }

        }

        if (!docConfig.isSynchronous()) {
            IoUtil.writeUtf8(new FileOutputStream(BASE_PATH + File.separator + StrUtil.removeSuffix(name, ".java") + ".json"), true, array);
        }
    }
}
