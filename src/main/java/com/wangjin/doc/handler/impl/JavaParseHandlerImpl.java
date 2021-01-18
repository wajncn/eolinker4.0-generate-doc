package com.wangjin.doc.handler.impl;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.wangjin.doc.handler.ParseHandler;
import lombok.SneakyThrows;

import java.nio.file.Path;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class JavaParseHandlerImpl implements ParseHandler<CompilationUnit> {

    @Override
    @SneakyThrows
    public CompilationUnit handler(Path path) {
        ParseResult<CompilationUnit> parse = getParse().parse(path);
        if (!parse.isSuccessful()) {
            throw new RuntimeException("解析失败 哦吼😯 -1");
        }
        return parse.getResult().orElseThrow(() -> new RuntimeException("解析失败 哦吼😯 -2"));
    }

}
