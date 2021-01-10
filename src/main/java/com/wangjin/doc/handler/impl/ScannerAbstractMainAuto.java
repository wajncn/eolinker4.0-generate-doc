package com.wangjin.doc.handler.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.handler.AbstractMain;
import kong.unirest.Unirest;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static com.wangjin.doc.base.Application.BASE_PATH;
import static com.wangjin.doc.base.Application.CONTROLLER_PATHS;
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
    public void init() {
        Properties properties = new Properties();
        try {
            File file = new File(BASE_PATH + File.separator + "config.properties");
            if (!file.exists()) {
                final File touch = FileUtil.touch(BASE_PATH, "config.properties");
                FileUtil.writeBytes(Base64.decodeStr(Unirest.get(Constant.CONFIG_PROPERTIES).asString().getBody()).getBytes(), touch);
                LocalFileSystem.getInstance().refresh(true);
                String message = "系统已初始配置文件[config.properties]在当前目录,请填写配置文件";
                Messages.showMessageDialog(message, "Info", Messages.getInformationIcon());
                printTips(message);
                file = touch;
            }
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            throw new FileNotFoundException("缺少配置文件 [config.properties]  请联系开发者");
        }

        String username = properties.getProperty("doc.username");
        String password = properties.getProperty("doc.password");
        String project_id = properties.getProperty("doc.project_id");
        String group_id = properties.getProperty("doc.group_id");
        // 如果没有配置组id,那就说明通过下拉框去选择的
        if (StrUtil.isBlank(group_id)) {
            //如果没配置组id 这里就不自动生成文档啦.
            create.set(false);
        }

        boolean update = Boolean.parseBoolean(properties.getProperty("doc.update"));

        DocConfig.init(DocConfig.builder().controllerPaths(CONTROLLER_PATHS).projectPath(BASE_PATH)
                .username(username)
                .password(password)
                .projectId(project_id)
                .groupId(group_id)
                .synchronous(true)
                .update(update)
                .build());
    }

    @Override
    @SneakyThrows
    protected void createDoc() {
        DocConfig docConfig = DocConfig.get();
        PROJECT.init(StrUtil.trim(docConfig.getProjectPath()));
        docConfig.getControllerPaths().forEach(a -> {
            try {
                print("开始生成: {}", a);
                PROJECT.generate(a);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        print("success");
        openBrowse("https://doc.f.wmeimob.com/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
    }

    @Override
    protected void onSuccess() {

    }
}