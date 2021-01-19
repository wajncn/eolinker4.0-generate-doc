package com.wangjin.doc.base;

import com.intellij.openapi.project.Project;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.handler.impl.ScannerAbstractMainAuto;
import com.wangjin.doc.util.BaseUtils;
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
    /**
     * 当前项目的绝对路径
     */
    public static String BASE_PATH = null;

    /**
     * 当前项目
     */
    public static Project PROJECT = null;

    /**
     * 选择controller绝对路径
     */
    public static List<String> CONTROLLER_PATHS = null;


    /**
     * 动态选择的组id,如果没有就从配置文件中拿
     */
    public static String GROUP_ID = null;

    public static String HOSTNAME = null;


    public static boolean LICENSE_STATUS = false;

    /**
     * 选择的方法块
     */
    @Getter
    private static final List<InterfaceDoc.MethodDoc> SELECTED_TEXT = new ArrayList<>();

    public static void addSelectText(InterfaceDoc.MethodDoc doc) {
        SELECTED_TEXT.add(doc);
    }


    public static void clear() {
        System.out.println("Clear Stack...");
        GROUP_ID = null;
        CONTROLLER_PATHS = null;
        PROJECT = null;
        BASE_PATH = null;
        SELECTED_TEXT.clear();
        AbstractMain.clear();
    }

    private static final AbstractMain abstractMain = new ScannerAbstractMainAuto();

    @SneakyThrows
    public static void execute(boolean init) {
        try {
            if (init) {
                AbstractMain.clear();
            }
            abstractMain.exe();
        } catch (NotUseException e) {
            BaseUtils.exit();
            throw new IllegalArgumentException(Constant.CHECK_URL_MSG);
        } catch (Exception e) {
            BaseUtils.printError("msg:{}", e.getMessage());
        }
    }

}
