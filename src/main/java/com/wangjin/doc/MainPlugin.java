package com.wangjin.doc;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.handler.ParseHandler;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: gen-doc-plugin
 * @description
 * @author: 王进
 **/
public class MainPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        handlerSelected(e);

        VirtualFile[] data = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (data == null || data.length == 0) {
            Messages.showMessageDialog(project, "请选择Controller", "Error", Messages.getInformationIcon());
            return;
        }
        Application.PROJECT = project;
        Application.BASE_PATH = project.getBasePath();
        Application.CONTROLLER_PATHS = Arrays.stream(data).map(file -> StrUtil.removePrefix(Paths.get(file.getPath()).toString(), "file:/")).collect(Collectors.toList());
        Application.execute();
    }


    private static final Pattern pattern = Pattern.compile(".*Mapping.*");  //匹配ER_所在的所有行

    private void handlerSelected(AnActionEvent e) {
        Application.getSELECTED_TEXT().clear();

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StrUtil.isEmpty(selectedText)) {
            return;
        }

        List<String> collect = ReUtil.findAllGroup0(pattern, selectedText).stream().map(StrUtil::trim).collect(Collectors.toList());

        collect.forEach(a -> {
            com.wangjin.doc.base.Project.getJavaParser().parseAnnotation(a).getResult().ifPresent(annotationExpr -> {
                InterfaceDoc.MethodDoc doc = new InterfaceDoc.MethodDoc();
                if (annotationExpr instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr s1 = (SingleMemberAnnotationExpr) annotationExpr;
                    doc.setRequestMapping(s1.getMemberValue().toString().replace("\"", ""));
                } else {
                    doc.setRequestMapping("/");
                }
                doc.setMethodType(InterfaceDoc.MethodType.valof(annotationExpr.getName().asString()));
                Application.addSelectText(doc);
            });
        });
    }
}
