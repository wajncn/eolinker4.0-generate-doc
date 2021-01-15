package com.wangjin.doc.handler.impl;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.handler.LoginDocHandler;
import com.wangjin.doc.utils.BaseUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;


@DisplayName("测试")
public class RequestInfoParseFilterImplTest {

    @Test
    void testFirstTest() throws IOException {
        Application.LICENSE_STATUS = true;
        Project.LICENSE_STATUS = true;
        Constant.LICENSE_STATUS = true;


        Application.BASE_PATH = "/Users/wangjin/IdeaProjects/yipin";

        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123456")
                .projectId("405")
                .groupId("2931")
                .synchronous(true)
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.BASE_PATH);
        project.generate("/Users/wangjin/IdeaProjects/yipin/yipin-admin/src/main/java/com/wmeimob/fastboot/yipin/controller/AdImagesController.java");

        BaseUtils.openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
    }
    @Test
    void test() {
        Application.LICENSE_STATUS = true;
        Constant.LICENSE_STATUS = true;
        Project.LICENSE_STATUS = true;
        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123456")
                .projectId("405").synchronous(true).build();
        DocConfig.init(docConfig);
        LoginDocHandler.login();
        System.out.println(LoginDocHandler.getGroupList());
    }
}