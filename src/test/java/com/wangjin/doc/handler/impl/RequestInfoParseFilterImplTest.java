package com.wangjin.doc.handler.impl;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
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
        project.generate("C:\\Users\\pc\\IdeaProjects\\guanghui-mini\\guanghui-wechat\\src\\main\\java\\com\\wmeimob\\fastboot\\guanghui\\controller\\WxUsersController.java");

        System.out.println("我的第一个测试开始测试");
    }
}