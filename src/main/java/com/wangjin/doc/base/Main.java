package com.wangjin.doc.base;

import com.intellij.openapi.project.Project;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.handler.impl.ScannerAbstractMainAuto;
import com.wangjin.doc.utils.BaseUtils;
import lombok.SneakyThrows;

import java.util.List;


/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class Main {

    public static final String VERSION = "5.2.2";
    /**
     * 项目的路径地址 运行jar包后用户捕获jar包所在的文件目录
     */
    public static String BASE_PATH = null;
    public static Project PROJECT = null;
    public static List<String> CONTROLLER_PATHS = null;

    @SneakyThrows
    public static void execute() {
        AbstractMain abstractMain = new ScannerAbstractMainAuto();
        try {
            abstractMain.exe();
        } catch (NotUseException e) {
            //双重退出,防止打包exe4j出现程序不退出问题
            BaseUtils.exit();
            throw new IllegalArgumentException(Constant.CHECK_URL_MSG);
        } catch (Exception e) {
            BaseUtils.printError("msg:{}", e.getMessage());
        }
    }
}
