package com.wangjin.doc.base;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.wangjin.doc.cache.FileCache;
import com.wangjin.doc.handler.DocHandler;
import com.wangjin.doc.handler.ParseHandler;
import com.wangjin.doc.handler.impl.DocHandlerImpl;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import com.wangjin.doc.utils.BaseUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class Project {

    public static final Project project = new Project();
    public static boolean LICENSE_STATUS = false;

    @Getter
    private static final JavaParser javaParser = new JavaParser();
    private static final DocHandler docHandler = new DocHandlerImpl();
    private static final ParseHandler<CompilationUnit> parseHandler = new JavaParseHandlerImpl();

    public final void init(String path) {
        FileCache.clear();
        //获取所有的java文件
        if (!FileUtil.isDirectory(path)) {
            throw new IllegalArgumentException("该路径不是目录");
        }
        List<File> files = FileUtil.loopFiles(path, pathname -> {
            String filter = pathname.getPath();
            return filter.endsWith(".java");
        });
        files.forEach(file -> FileCache.
                addFc(FileCache.FC.builder().
                        fileName(StrUtil.removeSuffix(file.getName(), ".java"))
                        .filePath(file.getPath()).build()));
    }

    public final void generate(String filePath) throws IOException {
        filePath = StrUtil.removeSuffix(filePath, ".java");
        FileCache.FC fc = FileCache.getFcWithController(filePath);
        if (fc == null) {
            BaseUtils.printError("{}.java 没有找到,请检测路径是否为绝对路径", filePath);
            return;
        }

        InterfaceDoc interfaceDoc = new InterfaceDoc();

        CompilationUnit compilationUnit = parseHandler.handler(Paths.get(fc.getFilePath()));

        Assert.isTrue(!compilationUnit.getTypes().isEmpty(), "系统检测该文档非标准Controller filePath:{}", filePath);

        TypeDeclaration<?> type = compilationUnit.getType(0);

        AnnotationExpr annotationExpr = type.getAnnotationByName("RequestMapping").orElse(null);
        if (annotationExpr == null) {
            BaseUtils.printWarn("该类缺少 [@RequestMapping(\"path\")] 已忽略生成文档   " + filePath);
            return;
        }
        if (!(annotationExpr instanceof SingleMemberAnnotationExpr)) {
            throw new IllegalArgumentException("请检测 @RequestMapping(\"path\") 写法是否正确");
        }
        String requestMapping = ((SingleMemberAnnotationExpr) annotationExpr).getMemberValue().toString().replace("\"", "");

        interfaceDoc.setFilePath(fc.getFilePath());
        interfaceDoc.setRequestMapping(StrUtil.addPrefixIfNot(requestMapping, "/"));
        interfaceDoc.setComment(type.getName() + "控制器");

        if (!Project.LICENSE_STATUS) {
            System.exit(1);
        }
        AtomicInteger index = new AtomicInteger(1);
        for (BodyDeclaration<?> member : type.getMembers()) {
            if (!(member instanceof MethodDeclaration)) {
//                暂不处理这种类型
                continue;
            }

            MethodDeclaration methodDeclaration = (MethodDeclaration) member;

            InterfaceDoc.MethodDoc doc = new InterfaceDoc.MethodDoc();
            doc.setComment(methodDeclaration.getComment().map(e -> BaseUtils.reformatMethodComment(e.getContent())).orElseGet(() -> "无注释方法" + index.getAndIncrement()));

            //判断是否为接口
            boolean isInterface = false;
            for (AnnotationExpr annotation : methodDeclaration.getAnnotations()) {
                if (!annotation.getName().toString().endsWith("Mapping")) {
                    continue;
                }

                isInterface = true;
                if (annotation instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr s1 = (SingleMemberAnnotationExpr) annotation;
                    doc.setRequestMapping(s1.getMemberValue().toString().replace("\"", ""));
                } else {
                    doc.setRequestMapping("/");
                }

                doc.setBody(methodDeclaration.getBody().map(BlockStmt::getStatements).orElseGet(NodeList::new));
                doc.setResponseObject(methodDeclaration.getTypeAsString());


                final Map<String, String> commentMap = BaseUtils.getCommentMap(methodDeclaration.getComment().map(Comment::getContent).orElse(null));
                List<InterfaceDoc.Args> args = methodDeclaration.getParameters().stream().map(p -> {
                    InterfaceDoc.Args arg = new InterfaceDoc.Args();
                    arg.setType(p.getTypeAsString());
                    arg.setField(p.getName().asString());
                    arg.setComment(Optional.ofNullable(commentMap.get(p.getName().asString())).orElse(""));
                    return arg;
                }).collect(Collectors.toList());


                doc.setRequestArgs(args);
                doc.setMethodType(InterfaceDoc.MethodType.valof(annotation.getName().asString()));

                //如果是分页的话:
                handlerRequestPageInfo(doc);
            }

            if (isInterface) {
                interfaceDoc.addMethodDoc(doc);
            }
        }

        //接口文档计算解析完成. 开始生产文档json
        docHandler.handler(interfaceDoc);
    }


    /**
     * 处理分页参数, 如果是分页的话,增加默认请求参数Integer,pageIndex
     *
     * @param doc
     */
    private void handlerRequestPageInfo(InterfaceDoc.MethodDoc doc) {
        if (!doc.getResponseObject().startsWith("PageInfo")) {
            return;
        }
        List<InterfaceDoc.Args> requestArgs = doc.getRequestArgs();
        //自动塞值进去:
        InterfaceDoc.Args pageIndex = new InterfaceDoc.Args();
        pageIndex.setType("Integer");
        pageIndex.setField("pageIndex");
        pageIndex.setComment("页码 默认为1");
        requestArgs.add(pageIndex);

        InterfaceDoc.Args pageSize = new InterfaceDoc.Args();
        pageSize.setType("Integer");
        pageSize.setField("pageSize");
        pageSize.setComment("分页大小 默认为20");
        requestArgs.add(pageSize);

        doc.setComment(doc.getComment() + "    (分页查询)");
    }
}
