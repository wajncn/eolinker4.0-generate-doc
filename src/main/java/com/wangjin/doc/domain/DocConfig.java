package com.wangjin.doc.domain;

import cn.hutool.core.lang.Assert;
import com.wangjin.doc.handler.LoginDocHandler;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
@Builder
@Getter
public class DocConfig {

    private static final ThreadLocal<DocConfig> CONFIG = new ThreadLocal<>();

    private final String projectPath;
    private final List<String> controllerPaths;
    private final String username;
    private final String password;
    private final String projectId;
    private final String groupId;
    private final boolean synchronous;
    private final boolean update;

    public static DocConfig get() {
        return CONFIG.get();
    }

    public static void init(DocConfig docConfig) {
        CONFIG.set(docConfig);
        if (docConfig.synchronous) {
            Assert.notEmpty(docConfig.getUsername(), "缺少配置属性: username");
            Assert.notEmpty(docConfig.getPassword(), "缺少配置属性: password");
            Assert.notEmpty(docConfig.getProjectId(), "缺少配置属性: project_id");
            Assert.notEmpty(docConfig.getGroupId(), "缺少配置属性: group_id");

            LoginDocHandler.login(docConfig.getUsername(), docConfig.getPassword());
        }
    }

}


