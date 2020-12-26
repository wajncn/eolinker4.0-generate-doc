package com.wangjin.doc.handler;

import java.nio.file.Path;


/**
 * 解析处理器
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@FunctionalInterface
public interface ParseHandler<T> {

    T handler(Path path);
}
