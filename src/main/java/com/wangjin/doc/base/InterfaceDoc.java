package com.wangjin.doc.base;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.Statement;
import com.wangjin.doc.util.BaseUtils;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 * @create: 2020-08-26 09:51
 **/
@Data
public class InterfaceDoc {

    private String comment;
    private String requestMapping;
    private String filePath;
    @Setter(value = AccessLevel.NONE)
    private List<MethodDoc> methodDoc = new ArrayList<>();

    public void setComment(String comment) {
        this.comment = BaseUtils.replaceIllegalityStr(comment);
    }

    public void addMethodDoc(MethodDoc doc) {
        methodDoc.add(doc);
    }

    @Getter
    @AllArgsConstructor
    public enum MethodType {
        POST("0"),
        GET("1"),
        PUT("2"),

        DELETE("3"),
        OPTION("5");

        private final String apiRequestType;


        public static MethodType valof(String type) {
            if ("GetMapping".equals(type)) {
                return GET;
            }
            if ("PostMapping".equals(type)) {
                return POST;
            }
            if ("DeleteMapping".equals(type)) {
                return DELETE;
            }
            if ("PutMapping".equals(type)) {
                return PUT;
            }

            return GET;
        }
    }

    @Data
    public static class MethodDoc {

        private String comment;
        private String requestMapping;
        private MethodType methodType;
        private List<Args> requestArgs;
        private String requestJson;
        private String responseJson;
        private String responseObject;
        private NodeList<Statement> body;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MethodDoc methodDoc = (MethodDoc) o;
            return Objects.equals(requestMapping, methodDoc.requestMapping) && methodType == methodDoc.methodType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(requestMapping, methodType);
        }

        public void setComment(String comment) {
            this.comment = BaseUtils.replaceIllegalityStr(comment);
        }
    }

    @Data
    public static class Args {
        private String type;
        private String field;
        private String comment;
        private boolean required;
    }
}
