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
import com.wangjin.doc.util.BaseUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@DisplayName("测试")
public class MainPluginTest {
    @Test
    void testFirstTest_win10() throws IOException, InterruptedException {
        Application.basePath = "C:\\Users\\pc\\IdeaProjects\\guanghui-mini";

        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123456")
                .projectId("408")
                .groupId("2962")
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.basePath);
        project.generate(Collections.singletonList("C:\\Users\\pc\\IdeaProjects\\guanghui-mini\\guanghui-wechat\\src\\main\\java\\com\\wmeimob\\fastboot\\guanghui\\controller\\employees\\EmployeesController.java"));
//        BaseUtils.openBrowse(docConfig.getUrl() + "/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());

        Thread.sleep(100000 * 5);
    }

    @Test
    void testFirstTest() throws IOException {
        Application.basePath = "/Users/wangjin/IdeaProjects/yipin";

        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123456")
                .projectId("408")
                .groupId("2963")
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.basePath);
        project.generate(Collections.singletonList("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/DesignOrderController.java"));
        BaseUtils.openBrowse(docConfig.getUrl() + "/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());

    }


    @Test
    void yipin() throws IOException, InterruptedException {

        long l = System.currentTimeMillis();
        Application.basePath = "/Users/wangjin/IdeaProjects/yipin";

        List<String> strings = new ArrayList<>();
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/AdImagesController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/BankCardController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/BillingRecordController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/CaseController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/CompleteTimeConfigController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/CouponController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/CouponRecordController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/DesignerController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/DesignerReviewController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/DesignOrderController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/ExpertiseConfigController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/GlobalConfigController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/IntegralController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/MerchantController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/MerchantReviewController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/OrderDetailController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/PrintOrderController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/ProtocolConfigController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/SwaggerTestController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/SysController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/TextController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/UserInfoController.java");
        strings.add("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/WithdrawRecordController.java");

        DocConfig docConfig = DocConfig.builder()
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
        Thread.sleep(3000*30);
    }


    protected final static ParseHandler<CompilationUnit> PARSE_HANDLER = new JavaParseHandlerImpl();
    //匹配ER_所在的所有行
    private static final Pattern MAPPING_PATTERN = Pattern.compile(".*Mapping.*");


    @Test
    void aaa(){
//        ReUtil.findAllGroup0(MAPPING_PATTERN, "@RequestMapping(value = \"adsfsf\",method = RequestMethod.GET)").stream()
        ReUtil.findAllGroup0(MAPPING_PATTERN, "@RequestMapping(\"aaa\")").stream()
                .map(StrUtil::trim)
                .collect(Collectors.toList()).forEach(a -> {
                    PARSE_HANDLER.getParse().parseAnnotation(a).getResult().ifPresent(annotationExpr -> {
                        InterfaceDoc.MethodDoc doc = new InterfaceDoc.MethodDoc();
                        if (annotationExpr instanceof SingleMemberAnnotationExpr) {
                            SingleMemberAnnotationExpr s1 = (SingleMemberAnnotationExpr) annotationExpr;
                            doc.setRequestMapping(s1.getMemberValue().toString().replace("\"", ""));
                        }else if(annotationExpr instanceof NormalAnnotationExpr){
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