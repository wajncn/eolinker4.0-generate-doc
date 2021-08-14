package com.wangjin.doc;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.handler.ParseHandler;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@DisplayName("测试")
public class MainPluginTest {


    @Test
    void yipin() throws IOException, InterruptedException {
        long l = System.currentTimeMillis();
        Application.basePath = "/Users/wangjin/IdeaProjects/yipin";
        List<String> strings = new ArrayList<>();
        strings.add("AdImagesController path");
        DocConfig docConfig = DocConfig.builder()
                .url("wangjin")
                .username("wangjin")
                .password("123456")
                .projectId("408")
                .groupId("2985")
                .controllerPaths(strings)
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.basePath);

        System.out.println("init time: " + (System.currentTimeMillis() - l) + "");
        project.generate(docConfig.getControllerPaths());
        System.out.println("generate time: " + (System.currentTimeMillis() - l) + "");
//        BaseUtils.openBrowse(docConfig.getUrl() + "/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
        System.out.println("---");
        Thread.sleep(3000 * 30);
    }


    protected final static ParseHandler<CompilationUnit> PARSE_HANDLER = new JavaParseHandlerImpl();
    //匹配ER_所在的所有行
    private static final Pattern MAPPING_PATTERN = Pattern.compile(".*Mapping.*");


    @Test
    void aaa() {
//        ReUtil.findAllGroup0(MAPPING_PATTERN, "@RequestMapping(value = \"adsfsf\",method = RequestMethod.GET)").stream()
        ReUtil.findAllGroup0(MAPPING_PATTERN, "@RequestMapping(\"aaa\")").stream()
                .map(StrUtil::trim)
                .collect(Collectors.toList()).forEach(a -> {
                    PARSE_HANDLER.getParse().parseAnnotation(a).getResult().ifPresent(annotationExpr -> {
                        InterfaceDoc.MethodDoc doc = new InterfaceDoc.MethodDoc();
                        if (annotationExpr instanceof SingleMemberAnnotationExpr) {
                            SingleMemberAnnotationExpr s1 = (SingleMemberAnnotationExpr) annotationExpr;
                            doc.setRequestMapping(s1.getMemberValue().toString().replace("\"", ""));
                        } else if (annotationExpr instanceof NormalAnnotationExpr) {
                            NormalAnnotationExpr s1 = (NormalAnnotationExpr) annotationExpr;
//                            doc.setRequestMapping(s1.getMemberValue().toString().replace("\"", ""));
                            System.out.println(1);
                        } else {
                            doc.setRequestMapping("/");
                        }
                        System.out.println(annotationExpr.getName());
                        doc.setMethodType(InterfaceDoc.MethodType.valof(annotationExpr.getName().asString()));
                        System.out.println(doc);
                    });
                });
    }

}