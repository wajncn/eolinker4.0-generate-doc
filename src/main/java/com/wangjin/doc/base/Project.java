package com.wangjin.doc.base;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
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
import com.wangjin.doc.handler.LoginDocHandler;
import com.wangjin.doc.handler.ParseHandler;
import com.wangjin.doc.handler.impl.DocHandlerImpl;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import com.wangjin.doc.util.BaseUtils;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wangjin.doc.util.BaseUtils.print;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class Project {
    public static final Project INSTANCE = new Project();
    private static final DocHandler DOC_HANDLER = new DocHandlerImpl();
    private static final ParseHandler<CompilationUnit> PARSE_HANDLER = new JavaParseHandlerImpl();
    //项目路径
    public static String path = null;
    //哪个模块的controller
    public static String module = null;

    public static String getModuleName(String file) {
        if (BaseUtils.isWindows()) {
            return file.replace(Project.path, "").split(File.separator + File.separator)[1];
        }
        return file.replace(Project.path, "").split(File.separator)[1];
    }

    public final void clear() {
        Project.path = null;
        Project.module = null;
        FileCache.clear();
    }

    public final void init(final String path) {
        clear();
        //获取所有的java文件
        if (!FileUtil.isDirectory(path)) {
            throw new IllegalArgumentException("该路径不是目录");
        }
        ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.AbortPolicy());

        List<CompletableFuture<List<File>>> collect = Arrays.stream(FileUtil.ls(path)).map(file -> {
            return CompletableFuture.supplyAsync(() -> {
                return FileUtil.loopFiles(file, pathname -> {
                    String filter = pathname.getPath();
                    return filter.endsWith(".java");
                });
            }, service);
        }).collect(Collectors.toList());
        ArrayList<File> files = collect.stream().map(CompletableFuture::join)
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        Project.path = path;
        files.forEach(file -> FileCache.
                addFc(FileCache.FC.builder().
                        fileName(StrUtil.removeSuffix(file.getName(), ".java"))
                        .module(getModuleName(file.getPath()))
                        .filePath(file.getPath()).build()));
    }

    public final void generate(final List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }
        Project.module = getModuleName(filePaths.get(0));

        LoginDocHandler.UploadDoc uploadDoc = new LoginDocHandler.UploadDoc();
        Thread thread = new Thread(uploadDoc, "uploadDoc");
        thread.setDaemon(true);
        thread.start();

        filePaths.forEach(file -> {
            try {
                print("开始生成: {}", file);
                generate(StrUtil.removeSuffix(file, ".java"));
            } catch (Exception e) {
                BaseUtils.printError("generate Exception", e);
            }
        });
        uploadDoc.setExecute(true);
    }

    /**
     * @param filePath 不要带.java后缀
     * @throws IOException
     */
    private void generate(final String filePath) throws IOException {
        FileCache.FC fc = FileCache.getFcWithController(filePath);
        if (fc == null) {
            BaseUtils.printError("{}.java 没有找到,请检测路径是否为绝对路径", filePath);
            return;
        }

        InterfaceDoc interfaceDoc = new InterfaceDoc();

        CompilationUnit compilationUnit = PARSE_HANDLER.handler(Paths.get(fc.getFilePath()));

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
        String requestMapping = ((SingleMemberAnnotationExpr) annotationExpr).getMemberValue().toString()
                .replace("\"", "");

        interfaceDoc.setFilePath(fc.getFilePath());
        interfaceDoc.setRequestMapping(StrUtil.addPrefixIfNot(requestMapping, "/"));
        interfaceDoc.setComment(type.getName() + "控制器");

        AtomicInteger index = new AtomicInteger(1);
        for (BodyDeclaration<?> member : type.getMembers()) {
            if (!(member instanceof MethodDeclaration)) {
//                暂不处理这种类型
                continue;
            }

            MethodDeclaration methodDeclaration = (MethodDeclaration) member;

            InterfaceDoc.MethodDoc doc = new InterfaceDoc.MethodDoc();
            doc.setComment(methodDeclaration.getComment()
                    .map(e -> BaseUtils.reformatMethodComment(e.getContent()))
                    .orElseGet(() -> Constant.NO_ANNOTATION_INTERFACE_TEXT + index.getAndIncrement()));

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


                final Map<String, String> commentMap = BaseUtils.getCommentMap(methodDeclaration.getComment()
                        .map(Comment::getContent).orElse(null));
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
        DOC_HANDLER.handler(interfaceDoc);
    }


    /**
     * 处理分页参数, 如果是分页的话,增加默认请求参数Integer,pageIndex
     *
     * @param doc
     */
    private void handlerRequestPageInfo(final InterfaceDoc.MethodDoc doc) {
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

        doc.setComment(doc.getComment().contains("分页") ?
                doc.getComment()
                :
                doc.getComment() + Constant.PAGE_QUERY_TEXT);
    }
}
