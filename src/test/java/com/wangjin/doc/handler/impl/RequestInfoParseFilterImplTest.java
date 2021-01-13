package com.wangjin.doc.handler.impl;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.handler.LoginDocHandler;
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

        Application.BASE_PATH = "C:\\Users\\pc\\IdeaProjects\\guanghui-mini";

        Project project = new Project();
        project.init(Application.BASE_PATH);
        project.generate("C:\\Users\\pc\\IdeaProjects\\guanghui-mini\\guanghui-admin\\src\\main\\java\\com\\wmeimob\\fastboot\\guanghui\\controller\\ActivityConfigController.java");

        System.out.println("我的第一个测试开始测试");
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