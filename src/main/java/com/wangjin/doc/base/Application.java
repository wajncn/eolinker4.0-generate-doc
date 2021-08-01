package com.wangjin.doc.base;

import com.intellij.openapi.project.Project;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.handler.impl.ScannerAbstractMainAuto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;


/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class Application {
    public static final boolean LOG = true;
    private static final Application APPLICATION = new Application();
    private static final AbstractMain ABSTRACT_MAIN = new ScannerAbstractMainAuto();
    /**
     * 选择的方法块
     */
    @Getter
    private static final List<InterfaceDoc.MethodDoc> SELECTED_TEXT = new ArrayList<>();
    /**
     * 当前项目的绝对路径
     */
    public static String basePath = null;
    /**
     * 当前项目
     */
    public static Project project = null;
    /**
     * 选择controller绝对路径
     */
    public static List<String> controllerPaths = null;
    /**
     * 动态选择的组id,如果没有就从配置文件中拿
     */
    public static String groupId = null;

    public static Application builder(Project project, String basePath, List<String> controllerPaths, String groupId) {
        Application.project = project;
        Application.basePath = basePath;
        Application.controllerPaths = controllerPaths;
        Application.groupId = groupId;
        return APPLICATION;
    }

    public static Application setGroupId(String groupId) {
        Application.groupId = groupId;
        return APPLICATION;
    }

    public static void addSelectText(InterfaceDoc.MethodDoc doc) {
        SELECTED_TEXT.add(doc);
    }


    public static void clear() {
        System.out.println("Clear Stack...");
        groupId = null;
        controllerPaths = null;
        project = null;
        basePath = null;
        SELECTED_TEXT.clear();
        AbstractMain.clear();
    }


    @SneakyThrows
    public void execute(boolean init) {
        if (init) {
            AbstractMain.clear();
        }
        ABSTRACT_MAIN.exe();
    }

}
