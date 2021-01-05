package com.wangjin.doc.handler;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.gson.JsonArray;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.cache.FileCache;
import com.wangjin.doc.domain.RequestInfo;
import com.wangjin.doc.domain.ResultInfo;
import com.wangjin.doc.handler.impl.RequestInfoParseFilterImpl;
import com.wangjin.doc.handler.impl.ResponseInfoParseFilterImpl;
import com.wangjin.doc.utils.BaseUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wangjin.doc.utils.BaseUtils.paramTypeFormat;

/**
 * 解析过滤器 职责: 负责单独的解析功能.
 *
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 * @see RequestInfoParseFilterImpl 处理请求参数
 * @see ResponseInfoParseFilterImpl 处理返回值
 **/
public abstract class ParseFilter extends ParseFactory {

    public ParseFilter() {
        super(getInterfaceDoc());
    }

    protected abstract void filter(final InterfaceDoc.MethodDoc doc);


    /**
     * 解析当前类的参数
     *
     * @param jsonArray
     * @param members
     * @param request   是否是请求参数
     */
    protected final void parseMember(JsonArray jsonArray, NodeList<BodyDeclaration<?>> members, String parentName, boolean request) {
        if (members.isEmpty()) {
            return;
        }


//        NodeList<BodyDeclaration<?>> members = type.getMembers();
        members.forEach(member -> {
            if (!(member instanceof FieldDeclaration)) {
                return;
            }

            FieldDeclaration fieldDeclaration = (FieldDeclaration) member;
            if (fieldDeclaration.getVariables().isEmpty()) {
                return;
            }

            VariableDeclarator variableDeclarator = fieldDeclaration.getVariables().get(0);


            if ("serialVersionUID".equals(variableDeclarator.getName().asString())) {
                return;
            }


            //检测递归
            Map<String, Long> collect = Arrays.asList((parentName + "").split(">>")).stream().collect(Collectors.groupingBy(a -> a, Collectors.counting()));
            String variableDeclaratorName = variableDeclarator.getName().asString();
            if (collect.get(variableDeclaratorName) != null && collect.get(variableDeclaratorName) >= 1) {
                BaseUtils.printWarn("出现自引用属性:[{}], 最大递归深度默认为:2   :{}", variableDeclaratorName, parentName);
                return;
            }

            String typeName = variableDeclarator.getType().asString();
            FileCache.FC fc = FileCache.getFc(typeName);

            //子类
            FileCache.FC fcChild = null;

            String paramType;
            if (fc == null) {
                if (typeName.startsWith("List") || typeName.startsWith("ArrayList")) {
                    paramType = paramTypeFormat("List");
                    fcChild = FileCache.getFc(variableDeclarator.getType().getChildNodes().get(1).toString());
                } else {
                    paramType = paramTypeFormat(variableDeclarator.getTypeAsString());
                }
            } else {
                paramType = paramTypeFormat("Object");
                fcChild = FileCache.getFc(variableDeclarator.getType().getChildNodes().get(0).toString());
            }


            if (request) {
                jsonArray.add(GSON.toJsonTree(RequestInfo.builder()
                        .paramType(paramType)
                        .paramKey(Optional.ofNullable(parentName).orElse("") + variableDeclarator.getName().asString())
                        .paramName(BaseUtils.reformatMethodComment(fieldDeclaration.getComment().map(Comment::getContent).orElse("无注释")))
                        .build()));
            } else {
                jsonArray.add(GSON.toJsonTree(ResultInfo.builder()
                        .paramKey(Optional.ofNullable(parentName).orElse("") + variableDeclarator.getName().asString())
                        .paramName(BaseUtils.reformatMethodComment(fieldDeclaration.getComment().map(Comment::getContent).orElse("无注释")))
                        .paramType(paramType)
                        .build()));
            }


            //检测下有没有子类
            if (fcChild == null) {
                return;
            }

            TypeDeclaration<?> typeDeclaration = PARSE_HANDLER.handler(Paths.get(fcChild.getFilePath())).getTypes().get(0);
            if (typeDeclaration instanceof EnumDeclaration) {
                return;
            }


            try {
                this.parseExtend(jsonArray, typeDeclaration, Optional.ofNullable(parentName).orElse("") + variableDeclarator.getName().asString() + ">>"
                        , request);
            } catch (Exception ignored) {

            }

            //递归调用.....
            this.parseMember(jsonArray, typeDeclaration.getMembers(),
                    Optional.ofNullable(parentName).orElse("") + variableDeclaratorName + ">>"
                    , request);
        });
    }


    /**
     * 解析父类的参数
     *
     * @param jsonArray
     * @param type
     */
    protected final void parseExtend(JsonArray jsonArray, TypeDeclaration<?> type, String parentName, boolean request) {
        if (!(type instanceof ClassOrInterfaceDeclaration)) {
            return;
        }

        //获取继承的父类
        NodeList<ClassOrInterfaceType> extendedTypes = ((ClassOrInterfaceDeclaration) type).getExtendedTypes();
        if (extendedTypes.isEmpty()) {
            return;
        }


        extendedTypes.forEach(et -> {
            FileCache.FC fc = FileCache.getFc(et.getName().asString());
            if (fc == null) {
                //普通类型的,无需考虑
                return;
            }

            try {
                ParseResult<CompilationUnit> parseResult = Project.getJavaParser().parse(Paths.get(fc.getFilePath()));
                parseResult.getResult().ifPresent(cu -> {
                    NodeList<BodyDeclaration<?>> members1 = cu.getType(0).getMembers();
                    this.parseMember(jsonArray, members1, parentName, request);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
