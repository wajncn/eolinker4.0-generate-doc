package com.wangjin.doc.base;

import com.google.gson.Gson;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class Constant {
    public static final String LICENSE = "https://javanet123.com/gendoc/license";
    public static final String CONFIG_PROPERTIES = "https://javanet123.com/gendoc/config_properties";
    public static final String CHECK_URL_MSG = "服务器繁忙,请稍后再试";
    public static boolean LICENSE_STATUS = false;
    public static final Gson GSON = new Gson();


}
