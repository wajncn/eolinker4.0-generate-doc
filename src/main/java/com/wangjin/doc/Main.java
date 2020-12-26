package com.wangjin.doc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.handler.AbstractMain;
import com.wangjin.doc.handler.impl.ScannerAbstractMainAuto;
import com.wangjin.doc.utils.BaseUtils;
import lombok.SneakyThrows;

import java.nio.file.Paths;
import java.util.List;


/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class Main {

    public static final String VERSION = "5.2.2";

//    private static final String EXE4_PATH = System.getProperty("exe.path");
//    private static final String p = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    /**
     * 项目的路径地址 运行jar包后用户捕获jar包所在的文件目录
     */
    public static String BASE_PATH = null;
    public static List<String> CONTROLLER_PATHS = null;

    @SneakyThrows
    public static void execute() {
        AbstractMain abstractMain = new ScannerAbstractMainAuto();
        try {
            abstractMain.exe();
        } catch (NotUseException e) {
            //双重退出,防止打包exe4j出现程序不退出问题
            System.exit(1);
            throw new IllegalArgumentException(Constant.CHECK_URL_MSG);
        } catch (Exception e) {
            BaseUtils.printError("msg:{}", e.getMessage());
        }
        BaseUtils.printTips("3秒后程序自动退出");
        Thread.sleep(3000);
    }
}
