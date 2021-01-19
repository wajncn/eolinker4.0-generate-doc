package com.wangjin.doc.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.ResultInfo;
import com.wangjin.doc.unirest.Unirest;
import lombok.Getter;

import java.awt.*;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.wangjin.doc.base.Constant.GSON;


/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 * @create: 2020-09-12 11:56
 **/
public class BaseUtils {


    /**
     * 忽略的文件
     */
    @Getter
    private static final List<String> IGNORE_FILE = new ArrayList<String>(12) {{
        this.add(".");
        this.add(".git");
        this.add(".idea");
        this.add("logs");
        this.add("static");
    }};


    /**
     * 把java的参数类型转换为doc文档中的类型
     */
    private static final Map<String, String> DOC_PARAM_MAP = new HashMap<String, String>(32) {{
        this.put("String", "0");
        this.put("file", "1");
        this.put("json", "2");
        this.put("int", "3");
        this.put("Integer", "3");
        this.put("float", "4");
        this.put("Float", "4");
        this.put("double", "5");
        this.put("Double", "5");
        this.put("date", "6");
        this.put("datetime", "7");
        this.put("Date", "7");
        this.put("boolean", "8");
        this.put("Boolean", "8");

        this.put("List", "12");
        this.put("ArrayList", "12");
        this.put("Object", "13");
    }};


    /**
     * 新框架的分页参数
     */
    @Getter
    private static final List<JsonElement> PAGE_INFO_new_framework = new ArrayList<JsonElement>(64) {{
        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageNum")
                .paramName("当前页")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageSize")
                .paramName("每页的数量")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("total")
                .paramName("总记录数")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("isLastPage")
                .paramName("是否为最后一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("list")
                .paramName("结果集")
                .paramType(paramTypeFormat("List"))
                .build()));
    }};


    /**
     * 分页的参数
     */
    @Getter
    private static final List<JsonElement> PAGE_INFO = new ArrayList<JsonElement>(64) {{
        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageNum")
                .paramName("当前页")
                .paramType(paramTypeFormat("int"))
                .build()));


        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pageSize")
                .paramName("每页的数量")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("size")
                .paramName("当前页的数量")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("pages")
                .paramName("总页数")
                .paramType(paramTypeFormat("int"))
                .build()));


        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("prePage")
                .paramName("前一页")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("nextPage")
                .paramName("下一页")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("isFirstPage")
                .paramName("是否为第一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("isLastPage")
                .paramName("是否为最后一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("hasPreviousPage")
                .paramName("是否有前一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("hasNextPage")
                .paramName("是否有下一页")
                .paramType(paramTypeFormat("boolean"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("total")
                .paramName("总记录数")
                .paramType(paramTypeFormat("int"))
                .build()));

        this.add(GSON.toJsonTree(ResultInfo.builder().paramKey("list")
                .paramName("结果集")
                .paramType(paramTypeFormat("List"))
                .build()));

    }};

    private static String VERSION;

    public static String getVersion() {
        return VERSION;
    }

    public static void printError(String template, Object... values) {
        Console.error("error: ".concat(template), values);
        MyNotifier.notifyError(StrUtil.format(template, values));
    }

    public static void printWarn(String template, Object... values) {
        Console.log("warning: ".concat(template), values);
        MyNotifier.notifyWarn(StrUtil.format(template, values));
    }

    public static void printScanner(String msg) {
        System.out.print(msg);
    }

    public static void print(String template, Object... values) {
        if (StrUtil.isBlank(template)) {
            print();
            return;
        }
        Console.log("info: ".concat(template), values);
        MyNotifier.notifyInfo(StrUtil.format(template, values));
    }

    public static void print() {
        System.out.println();
    }

    public static void printTips(String template, Object... values) {
        Console.log("tips: ".concat(template), values);
        MyNotifier.notifyInfo(StrUtil.format(template, values));
    }


    public static boolean isIgnore(String str) {
        for (String s : IGNORE_FILE) {
            if (s.equals(str) || s.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把java的参数类型转换为doc文档中的类型
     *
     * @param paramType
     * @return
     */
    public static String paramTypeFormat(String paramType) {
        String string = DOC_PARAM_MAP.get(paramType);
        return string == null ? "0" : string;
    }


    /**
     * 转义一些乱七八糟的非法字符,防止程序正则或者解析异常
     * 该方法不能 trim()
     *
     * @param s
     * @return
     */
    public static String replaceIllegalityStr(String s) {
        return StrUtil.removeAllLineBreaks(s)
                .replace("!", "-")
                .replace("@", "-")
                .replace("#", "-")
                .replace("$", "-")
                .replace("%", "-")
                .replace("^", "-")
                .replace("&", "-")
                .replace("*", "-")
                .replace("<", "(")
                .replace(">", ")")
                .replace("/", "-")
                .replace("\\", "-")
                ;
    }

    public static boolean isMac() {
        return "Mac OS X".equalsIgnoreCase(System.getProperty("os.name"));
    }


    /**
     * 主要获取方法的注释. 不包括参数的注释
     *
     * @param s
     * @return
     */
    public static String reformatMethodComment(String s) {
        //doc 系统说明最大支持100个字符
        return reformatMethodComment(s, 100);
    }


    public static String reformatMethodComment(String s, int length) {
        if (s == null) {
            return "";
        }
        s = ReUtil.replaceAll(s, "@.*", "");
        s = s.replace("*", "");
        s = StrUtil.trim(s);
        return StrUtil.maxLength(s, length);
    }


    /**
     * 检测客户端是否还能使用. 异步调用的时候改方法不能抛出异常. 打包进ex4j会出现程序正常使用问题
     *
     * @return
     */
    public static boolean checkVersion() {
        try {
            JsonObject jsonObject = JsonParser.parseString(Base64.decodeStr(Unirest.get(Constant.LICENSE).asString().getBody()).trim()).getAsJsonObject();
            return Project.LICENSE_STATUS = jsonObject.get("status").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 该方法主要是把方法注释的param参数解析成map
     *
     * @param str
     * @return
     */
    public static Map<String, String> getCommentMap(String str) {
        Map<String, String> map = new HashMap<>(12);
        if (str == null) {
            return map;
        }
        String all = ReUtil.get("\\@param.*", str, 0);
        if (all == null) {
            return map;
        }

        AtomicReference<String> key = new AtomicReference<>();
        Arrays.asList(all.split("\n")).forEach(sp -> {
            sp = sp.trim();
            if (sp.contains("@param")) {
                key.set(ReUtil.get(".*@param\\s+(\\S+)", sp, 1));
                sp = ReUtil.replaceAll(sp, ".*@param\\s+\\S+", "");
                sp = sp.trim();
            } else {
                sp = StrUtil.removePrefix(sp, "*");
                sp = sp.trim();
                sp = ReUtil.replaceAll(sp, "\\s+", " ");
                //这里增加个容错,防止解析出现@return问题.
                if (sp.startsWith("@return")) {
                    return;
                }
            }
            final String finalSp = sp;
            map.put(key.get(), Optional.ofNullable(map.get(key.get()))
                    .map(a -> a.concat("\n").concat(finalSp)).orElse(sp));
        });
        return map;
    }


    /**
     * 普通的md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        return new BigInteger(1, digest).toString(16);
    }

    public static void exit() {
        System.exit(1);
    }


    public static void openBrowse(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                System.setProperty("java.awt.headless", "false");
                // 创建一个URI实例
                URI uri = URI.create(url);
                // 获取当前系统桌面扩展
                Desktop dp = Desktop.getDesktop();
                // 判断系统桌面是否支持要执行的功能
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    // 获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            } catch (Exception e) {
                printWarn("打开浏览器失败: url:{}", url);
            }
        } else {
//            print("打开浏览器失败: ",url);
        }
    }
}
