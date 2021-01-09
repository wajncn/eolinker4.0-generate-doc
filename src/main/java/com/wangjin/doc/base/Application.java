package com.wangjin.doc.base;

import com.intellij.openapi.project.Project;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.handler.impl.ScannerAbstractMainAuto;
import com.wangjin.doc.utils.BaseUtils;
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

    public static final String VERSION = "5.2.3";
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
     * 选择的方法块
     */
    @Getter
    private static final List<InterfaceDoc.MethodDoc> SELECTED_TEXT = new ArrayList<>();

    public static void addSelectText(InterfaceDoc.MethodDoc doc) {
        SELECTED_TEXT.add(doc);
    }


    @SneakyThrows
    public static void execute() {
        AbstractMain abstractMain = new ScannerAbstractMainAuto();
        try {
            abstractMain.exe();
        } catch (NotUseException e) {
            BaseUtils.exit();
            throw new IllegalArgumentException(Constant.CHECK_URL_MSG);
        } catch (Exception e) {
            BaseUtils.printError("msg:{}", e.getMessage());
        }
    }
}
