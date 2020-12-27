package com.wangjin.doc.handler.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.ui.Messages;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.handler.AbstractMain;
import kong.unirest.Unirest;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static com.wangjin.doc.base.Main.BASE_PATH;
import static com.wangjin.doc.base.Main.CONTROLLER_PATHS;
import static com.wangjin.doc.utils.BaseUtils.print;
import static com.wangjin.doc.utils.BaseUtils.printTips;

/**
 * 自动执行
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ScannerAbstractMainAuto extends AbstractMain {

    @Override
    @SneakyThrows
    protected void init() {
        AUTO.set(true);
        Properties properties = new Properties();
        try {
            File file = new File(BASE_PATH + File.separator + "config.properties");
            if (!file.exists()) {
                byte[] body = Unirest.get("https://file.javanet123.com/gendoc/config.properties.template").asBytes().getBody();
//                ClassPathResource resource = new ClassPathResource(StrUtil.removePrefix(this.getClass().getResource("/config.properties.template").getPath(),"file:/"));
                //直接给初始化一个 容错处理
                final File touch = FileUtil.touch(BASE_PATH, "config.properties");
                FileUtil.writeBytes(body, touch);
                Messages.showMessageDialog("系统已初始配置文件[config.properties]在当前目录", "Info", Messages.getInformationIcon());
                printTips("系统已初始配置文件[config.properties]在当前目录");
                file = touch;
            }
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            throw new FileNotFoundException("缺少配置文件 [config.properties]  请联系开发者");
        }

        String project_path = BASE_PATH;
        List<String> controller_paths = CONTROLLER_PATHS;
        String username = properties.getProperty("doc.username");
        String password = properties.getProperty("doc.password");
        String project_id = properties.getProperty("doc.project_id");
        String group_id = properties.getProperty("doc.group_id");

        DocConfig.init(DocConfig.builder().controllerPaths(controller_paths).projectPath(project_path)
                .username(username)
                .password(password)
                .projectId(project_id)
                .groupId(group_id)
                .synchronous(true)
                .build());
    }

    @Override
    @SneakyThrows
    protected void createDoc() {
        final Project project = Project.project;
        DocConfig docConfig = DocConfig.get();
        project.init(StrUtil.trim(docConfig.getProjectPath()));

        docConfig.getControllerPaths().forEach(a -> {
            print("开始生成: {}", a);
            try {
                project.gen(a);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        print("success");

        if (docConfig.isSynchronous()) {
            openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
        }
    }
}