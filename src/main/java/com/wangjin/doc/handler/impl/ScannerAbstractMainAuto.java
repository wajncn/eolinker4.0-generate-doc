package com.wangjin.doc.handler.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.unirest.Unirest;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import static com.wangjin.doc.base.Application.basePath;
import static com.wangjin.doc.base.Application.controllerPaths;
import static com.wangjin.doc.util.BaseUtils.print;
import static com.wangjin.doc.util.BaseUtils.printTips;

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
            File file = new File(basePath + File.separator + Constant.DOC_CONFIG_PROPERTIES_NAME);
            if (!file.exists()) {
                final File touch = FileUtil.touch(basePath, Constant.DOC_CONFIG_PROPERTIES_NAME);
                FileUtil.writeBytes(Base64.decodeStr(Unirest.get(Constant.CONFIG_PROPERTIES).asString().getBody())
                        .getBytes(), touch);
                LocalFileSystem.getInstance().refresh(true);
                String message = "系统已初始配置文件[" + Constant.DOC_CONFIG_PROPERTIES_NAME + "]在当前项目根目录,请填写配置文件重新尝试";
                Messages.showMessageDialog(message, "Info", Messages.getInformationIcon());
                printTips(message);
                file = touch;
            }
            properties.load(new FileInputStream(file));
        } catch (Exception e) {
            throw new FileNotFoundException("缺少配置文件 [" + Constant.DOC_CONFIG_PROPERTIES_NAME + "]  请联系开发者");
        }

        String username = properties.getProperty("doc.username");
        String password = properties.getProperty("doc.password");
        String project_id = properties.getProperty("doc.project_id");
        String group_id = properties.getProperty("doc.group_id");
        String url = properties.getProperty("doc.url");


        try {
            String ignoreResponse = properties.getProperty("doc.ignore_result");
            if (StrUtil.isNotBlank(ignoreResponse)) {
                for (String s : ignoreResponse.split(",")) {
                    ResponseInfoBaseFilterImpl.addIGNORE_RESULT(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 如果没有配置组id,那就说明通过下拉框去选择的
        if (StrUtil.isBlank(group_id)) {
            //如果没配置组id 这里就不自动生成文档啦.
            CREATE.set(false);
        }

        boolean update = Boolean.parseBoolean(properties.getProperty("doc.update"));

        DocConfig.init(DocConfig.builder().controllerPaths(controllerPaths).projectPath(basePath)
                .username(username)
                .password(password)
                .projectId(project_id)
                .groupId(group_id)
                .update(update)
                .url(StrUtil.removeSuffix(url, "/"))
                .build());
    }

    @Override
    @SneakyThrows
    protected void createDoc() {
        DocConfig docConfig = DocConfig.get();
        PROJECT.init(StrUtil.trim(docConfig.getProjectPath()));
        PROJECT.generate(docConfig.getControllerPaths());
        print("success");
        openBrowse(docConfig.getUrl() + "/index.html#/home/project/inside/api/list?projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId());
    }

    @Override
    protected void onSuccess() {

    }
}