package com.wangjin.doc.handler.impl;

import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.handler.DocHandler;
import com.wangjin.doc.handler.ParseFactory;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class DocHandlerImpl implements DocHandler {

    @Override
    public void handler(InterfaceDoc interfaceDoc) {
        new ParseFactory(interfaceDoc)
                .add(new RequestInfoParseFilterImpl())
                .add(new ResponseInfoParseFilterImpl())
                .exec();
    }
}
