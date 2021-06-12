package com.wangjin.doc;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: gen-doc-plugin
 * @description
 * @author: 王进
 **/
public class MainTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(1);

        long l = System.currentTimeMillis();

        Application.LICENSE_STATUS = true;
        Project.LICENSE_STATUS = true;
        Constant.LICENSE_STATUS = true;

        Application.BASE_PATH = "C:\\Users\\pc\\IdeaProjects\\chiniu";
        List<String> strings = new ArrayList<>();
        strings.add("C:\\Users\\pc\\IdeaProjects\\chiniu\\chiniu-admin\\src\\main\\java\\com\\wmeimob\\fastboot\\chiniu\\controller\\OrdersController.java");

        DocConfig docConfig = DocConfig.builder()
                .username("wangjin")
                .password("123Wmeimob456")
                .projectId("464")
                .groupId("3607")
                .controllerPaths(strings)
                .update(true)
                .build();
        DocConfig.init(docConfig);

        Project project = new Project();
        project.init(Application.BASE_PATH);

        System.out.println("init time: " + (System.currentTimeMillis() - l) + "");

        project.generate(docConfig.getControllerPaths());

        System.out.println("generate time: " + (System.currentTimeMillis() - l) + "");
//        BaseUtils.openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
        System.out.println("---");
        Thread.sleep(3000*30);
    }
}
