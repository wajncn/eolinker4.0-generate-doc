package com.wangjin.doc.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Constant;
import com.wangjin.doc.base.Project;
import kong.unirest.Unirest;
import lombok.Getter;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 * @create: 2020-09-12 11:56
 **/
public class BaseUtils {

    @Getter
    private static final List<String> ignore_file = new ArrayList<String>(32) {{
        this.add(".");
        this.add(".git");
        this.add(".idea");
        this.add("logs");
        this.add("static");
    }};
    private static final Map<String, String> map = new HashMap<String, String>(32) {{
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
        for (String s : ignore_file) {
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
        String string = map.get(paramType);
        return string == null ? "0" : string;
    }


    /**
     * 转义一些乱七八糟的非法字符,防止程序正则或者解析异常
     *
     * @param srt
     * @return
     */
    public static String replaceIllegalityStr(String srt) {
        return StrUtil.removeAllLineBreaks(srt)
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
        s = ReUtil.replaceAll(s, "@.*", "");
        s = s.replace("*", "");
        s = StrUtil.trim(s);
        return s;
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
            map.put(key.get(), Optional.ofNullable(map.get(key.get())).map(a -> a.concat(finalSp)).orElse(sp));
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
}
