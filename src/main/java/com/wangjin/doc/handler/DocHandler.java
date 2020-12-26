package com.wangjin.doc.handler;

import com.wangjin.doc.base.InterfaceDoc;

/**
 * 接口文档处理器
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@FunctionalInterface
public interface DocHandler {

    void handler(InterfaceDoc interfaceDoc);
}
