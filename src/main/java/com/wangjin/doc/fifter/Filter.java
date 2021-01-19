package com.wangjin.doc.fifter;

import com.wangjin.doc.base.InterfaceDoc;

/**
 * 过滤器
 * 主要用户处理文档,对参数进行过滤处理,比如请求参数,返回参数等
 */
public interface Filter {

    /**
     * 主要用户处理文档,对参数进行过滤处理,比如请求参数,返回参数等
     *
     * @param doc
     */
    void filter(InterfaceDoc.MethodDoc doc);
}
