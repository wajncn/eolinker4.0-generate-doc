package com.wangjin.doc;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangjin.doc.base.Application;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @program: gen-doc-plugin
 * @description
 * @author: 王进
 **/
public class MainPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile[] data = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (data == null || data.length == 0) {
            Messages.showMessageDialog(project, "请选择Controller", "Error", Messages.getInformationIcon());
            return;
        }
        Application.PROJECT = project;
        Application.BASE_PATH = project.getBasePath();
        Application.CONTROLLER_PATHS = Arrays.stream(data).map(file -> StrUtil.removePrefix(Paths.get(file.getPath()).toString(), "file:/")).collect(Collectors.toList());
        Application.execute();
    }
}
