package com.wangjin.doc.handler;

import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.exceptions.NotUseException;
import com.wangjin.doc.util.BaseUtils;
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;

import static com.wangjin.doc.util.BaseUtils.checkVersion;
import static com.wangjin.doc.util.BaseUtils.print;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public abstract class AbstractMain {
    protected static final Project PROJECT = Project.project;

    /**
     * 是否生成文档
     */
    protected static final ThreadLocal<Boolean> create = ThreadLocal.withInitial(() -> true);


    /**
     * 是否初始化
     */
    private static volatile boolean init = true;

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

    public static void clear() {
        init = true;
        create.remove();
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
                return Constant.LICENSE_STATUS = true;
            } else {
                BaseUtils.exit();
                return false;
            }
        });

        for (; ; ) {
            if (!voidCompletableFuture.isDone()) {
                sleep();
            } else {
                boolean join = Application.LICENSE_STATUS = voidCompletableFuture.join();
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
    protected abstract void init();

    protected abstract void createDoc();

    protected abstract void onSuccess();


    public final void exe() {
        try {
            if (init) {
                this.printHead();
                this.check(true);
                this.init();
                init = false;
            }
            if (create.get()) {
                LoginDocHandler.refreshApiList();
                this.createDoc();
            }
            create.remove();
        } finally {
            onSuccess();
        }
    }


    protected void openBrowse(String url) {
        BaseUtils.openBrowse(url);
    }
}
