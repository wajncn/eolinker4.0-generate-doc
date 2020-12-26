package com.wangjin.doc;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.SystemIndependent;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        final String basePath = project.getBasePath();
        System.out.println("project.getBasePath():" + basePath);

        Main.BASE_PATH = project.getBasePath();
        VirtualFile[] data = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (data == null || data.length == 0) {
            Messages.showMessageDialog(project, "请选择Controller", "Error", Messages.getInformationIcon());
            return;
        }

        List<String> collect = Arrays.stream(data).map(file -> {
            return StrUtil.removePrefix(Paths.get(file.getPath()).toString(), "file:/");
        }).collect(Collectors.toList());
        Main.CONTROLLER_PATHS = collect;

        Main.BASE_PATH = basePath;
        Main.execute();
    }
}
