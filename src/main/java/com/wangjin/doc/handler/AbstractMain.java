package com.wangjin.doc.handler;

import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.utils.BaseUtils;
import lombok.SneakyThrows;

import java.awt.*;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static com.wangjin.doc.utils.BaseUtils.*;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public abstract class AbstractMain {

    protected static final ThreadLocal<Boolean> AUTO = ThreadLocal.withInitial(() -> true);


    @SneakyThrows
    protected final void sleep() {
        sleep(300);
    }

    @SneakyThrows
    protected final void sleep(long millis) {
        Thread.sleep(millis);
    }

    /**
     * 打印头部信息
     */
    private void printHead() {
//        print(Constant.HEADER_TEXT);
        print("developer: 第一交付中心-开发四组");
    }


    /**
     * 异步检测程序非法性
     *
     * @param tips
     */
    @SneakyThrows
    protected final void check(boolean tips) {
        CompletableFuture<Boolean> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
            boolean r = checkVersion();
            if (r) {
                return true;
            } else {
//                print("");
//                printError("{}: {}", Constant.CHECK_URL_MSG, Constant.DOWNLOAD_URL);
//                print(Constant.AUTO_DOWNLOAD_TIPS);
//                sleep(2000);
//                openBrowse(Constant.DOWNLOAD_URL);
                BaseUtils.exit();
                return false;
            }
        });

        for (; ; ) {
            if (!voidCompletableFuture.isDone()) {
                sleep();
                if (tips) {
                    System.out.print(Constant.POINT);
                }
            } else {
                boolean join = voidCompletableFuture.join();
                if (join) {
                    System.out.println();
                    break;
                }
                BaseUtils.exit();
                throw new NotUseException();
            }
        }
    }


    /**
     * 打印头部信息
     */
    protected void init() {
        AUTO.set(true);
    }


    protected abstract void createDoc();


    public final void exe() {
        this.printHead();
        this.check(true);
        this.init();
        this.createDoc();
    }


    protected void openBrowse(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                System.setProperty("java.awt.headless", "false");
                // 创建一个URI实例
                URI uri = URI.create(url);
                // 获取当前系统桌面扩展
                Desktop dp = Desktop.getDesktop();
                // 判断系统桌面是否支持要执行的功能
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    // 获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            } catch (Exception e) {
                printWarn("打开浏览器失败: url:{}", url);
            }
        } else {
//            print("打开浏览器失败: ",url);
        }
    }
}
