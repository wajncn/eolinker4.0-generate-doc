package com.wangjin.doc.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.handler.AbstractMain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

import static com.wangjin.doc.utils.BaseUtils.print;
import static com.wangjin.doc.utils.BaseUtils.printScanner;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class ScannerAbstractMain extends AbstractMain {

    private static LocalDateTime GEN_TIME = null;
    private static String FILE_PATH = null;

    @Override
    protected void init() {
        AUTO.set(false);
    }

    @Override
    protected void createDoc() {
        final Project project = Project.project;
        final Scanner scanner = new Scanner(System.in);

        boolean init;
        do {
            try {
                printScanner("请输入您要解析的项目绝对路径:");
                FILE_PATH = StrUtil.trim(scanner.next());
                project.init(FILE_PATH);
                init = false;
            } catch (Exception e) {
                init = true;
                e.printStackTrace();
                sleep();
            }
        } while (init);

        for (; ; ) {
            boolean gen;
            do {
                try {
                    printScanner("请输入Controller的绝对路径 即可生成文档:");
                    String filePath = StrUtil.trim(StrUtil.removeSuffix(scanner.next(), ".java"));

                    //增加判断,如果间隔时间超过1分钟, 则重新 init,防止文件变化
                    //如果间隔时间超过1小时, 则检测版本是否正常
                    Optional.ofNullable(GEN_TIME).ifPresent(genTime -> {
                        if (Duration.between(genTime, LocalDateTime.now()).toMinutes() >= 1) {
                            project.init(FILE_PATH);
                        }
                        if (Duration.between(genTime, LocalDateTime.now()).toHours() >= 1) {
                            check(false);
                        }
                    });

                    project.gen(filePath);
                    print("success ==> 接口文档以生成在当前目录");
                    GEN_TIME = LocalDateTime.now();
                    print("");
                    gen = false;
                } catch (Exception e) {
                    gen = true;
                    e.printStackTrace();
                    sleep();
                }
            } while (gen);
        }
    }
}