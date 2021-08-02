package com.wangjin.doc.handler;

import com.wangjin.doc.base.Project;
import com.wangjin.doc.util.BaseUtils;

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
    protected static final ThreadLocal<Boolean> CREATE_DOC = ThreadLocal.withInitial(() -> true);


    /**
     * 是否初始化
     */
    private static volatile boolean init = true;

    public static void clear() {
        init = true;
        CREATE_DOC.remove();
    }

    /**
     * 打印头部信息
     */
    private void printHead() {
    }

    /**
     * init
     */
    protected abstract void init();

    protected abstract void createDoc();

    protected abstract void onSuccess();

    protected static final Object lock = new Object();

    public final void exe() {
        try {
            if (init) {
                this.printHead();
                this.init();
                init = false;
            }
            if (CREATE_DOC.get()) {
                LoginDocHandler.refreshApiList();
                this.createDoc();
            }
        } finally {
            if (CREATE_DOC.get()) {
                onSuccess();
            }
            CREATE_DOC.remove();
        }
    }


    protected void openBrowse(String url) {
        BaseUtils.openBrowse(url);
    }
}
