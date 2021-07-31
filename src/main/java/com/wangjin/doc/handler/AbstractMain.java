package com.wangjin.doc.handler;

import com.wangjin.doc.base.Project;
import com.wangjin.doc.util.BaseUtils;

import static com.wangjin.doc.util.BaseUtils.print;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public abstract class AbstractMain {
    protected static final Project PROJECT = Project.INSTANCE;

    /**
     * 是否生成文档
     */
    protected static final ThreadLocal<Boolean> create = ThreadLocal.withInitial(() -> true);


    /**
     * 是否初始化
     */
    private static volatile boolean init = true;

    /**
     * 打印头部信息
     */
    private void printHead() {
        print("开始运行...");
    }

    public static void clear() {
        init = true;
        create.remove();
    }

    /**
     * init
     */
    protected abstract void init();

    protected abstract void createDoc();

    protected abstract void onSuccess();


    public final void exe() {
        try {
            if (init) {
                this.printHead();
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
