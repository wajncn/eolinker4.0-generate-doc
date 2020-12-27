package com.wangjin.doc;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangjin.doc.base.Main;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**令牌无效,无法自动同步到文档系统
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
        Main.BASE_PATH = project.getBasePath();
        Main.PROJECT = project;
        Main.CONTROLLER_PATHS = Arrays.stream(data).map(file -> {
            return StrUtil.removePrefix(Paths.get(file.getPath()).toString(), "file:/");
        }).collect(Collectors.toList());
        Main.execute();
    }
}
