package com.wangjin.doc;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.handler.LoginDocHandler;
import com.wangjin.doc.handler.ParseHandler;
import com.wangjin.doc.handler.impl.JavaParseHandlerImpl;
import com.wangjin.doc.util.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: gen-doc-plugin
 * @description
 * @author: 王进
 **/
@SuppressWarnings("all")
public class MainPlugin extends AnAction {

    protected final static ParseHandler<CompilationUnit> PARSE_HANDLER = new JavaParseHandlerImpl();
    //匹配ER_所在的所有行
    private static final Pattern MAPPING_PATTERN = Pattern.compile(".*Mapping.*");

    @Override
    public void actionPerformed(AnActionEvent event) {
        Application.clear();
        try {
            Project project = event.getData(PlatformDataKeys.PROJECT);
            VirtualFile[] data = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
            if (data == null || data.length == 0) {
                Messages.showMessageDialog(project, "请选择Controller", "Error", Messages.getInformationIcon());
                return;
            }

            handlerSelected(event);

            Application.builder(project, project.getBasePath(), Arrays.stream(data)
                    .map(file -> StrUtil.removePrefix(Paths.get(file.getPath()).toString(), "file:/"))
                    .collect(Collectors.toList()), null).execute(true);
            //没有配置group_id 要自定义选择
            if (StrUtil.isEmpty(DocConfig.get().getGroupId())) {
                selectGroup(project);
            }
        } catch (Exception ignored) {
            BaseUtils.printError("Exception {}", ignored);
        } finally {
            Application.clear();
        }

    }

    /**
     * 增加下拉框
     *
     * @param project
     */
    private void selectGroup(Project project) {
        LinkedHashMap<String, String> groupListForMap = LoginDocHandler.getGroupListForMap();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JTextField textField = new JTextField();

        JComboBox<String> cmb = new JComboBox<String>();
        cmb.addItem("--请选择分组--");
//        cmb.setEditable(true);
        groupListForMap.keySet().forEach(cmb::addItem);
        panel.add(cmb, BorderLayout.NORTH);
        JButton button = new JButton("Generate");

        ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(panel, textField);
        JBPopup popup = componentPopupBuilder.createPopup();

        button.addActionListener(e1 -> {
            int selectedIndex = cmb.getSelectedIndex();
            if (selectedIndex == 0) {
                Messages.showMessageDialog(project, "请选择分组", "Error", Messages.getInformationIcon());
                return;
            }
            //这里动态设置分组,就无需从配置文件拿数据啦
            panel.setVisible(false);
            popup.setUiVisible(false);

            Application.setGroupId(groupListForMap.get(cmb.getSelectedItem().toString())).execute(false);
        });

        panel.add(button, BorderLayout.CENTER);
        popup.setMinimumSize(new Dimension(180, 0));
        popup.setRequestFocus(true);
        popup.showCenteredInCurrentWindow(project);
    }

    /**
     * 处理选择代码块
     *
     * @param e
     */
    private void handlerSelected(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StrUtil.isEmpty(selectedText)) {
            return;
        }

        ReUtil.findAllGroup0(MAPPING_PATTERN, selectedText).stream()
                .map(StrUtil::trim)
                .collect(Collectors.toList()).forEach(a -> {
                    PARSE_HANDLER.getParse().parseAnnotation(a).getResult().ifPresent(annotationExpr -> {
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
