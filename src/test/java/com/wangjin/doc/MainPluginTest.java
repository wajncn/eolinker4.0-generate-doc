package com.wangjin.doc;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.utils.BaseUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@DisplayName("测试")
public class MainPluginTest {

    @Test
    void testFirstTest() throws IOException {
        Application.LICENSE_STATUS = true;
        Project.LICENSE_STATUS = true;
        Constant.LICENSE_STATUS = true;


        Application.BASE_PATH = "/Users/wangjin/IdeaProjects/yipin";

        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123456")
                .projectId("408")
                .groupId("2963")
                .synchronous(true)
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.BASE_PATH);
        project.generate(Collections.singletonList("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/DesignOrderController.java"));
        BaseUtils.openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
    }


    @Test
    void yipin() throws IOException {

        long l = System.currentTimeMillis();

        Application.LICENSE_STATUS = true;
        Project.LICENSE_STATUS = true;
        Constant.LICENSE_STATUS = true;

        Application.BASE_PATH = "/Users/wangjin/IdeaProjects/yipin";

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
                .groupId("2964")
                .controllerPaths(strings)
                .synchronous(true)
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.BASE_PATH);

        System.out.println("init time: " + (System.currentTimeMillis() - l) + "");


        project.generate(docConfig.getControllerPaths());

        System.out.println("generate time: " + (System.currentTimeMillis() - l) + "");
        BaseUtils.openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
    }
}