package com.wangjin.doc.cache;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;


/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public final class FileCache {

    private static final Map<String, FC> FILE_CACHE = new HashMap<>();


    private static final Map<String, FC> FILE_CACHE_CONTROLLER = new HashMap<>();


    private FileCache() {

    }

    public static FC getFc(String fileName) {
        return FILE_CACHE.get(fileName);
    }


    /**
     * 根据绝对路径获取Fc
     *
     * @param filePath
     * @return
     */
    public static FC getFcWithController(String filePath) {
        return FILE_CACHE_CONTROLLER.get(filePath);
    }


    public static void clear() {
        FILE_CACHE.clear();
        FILE_CACHE_CONTROLLER.clear();
    }


    public static void addFc(FC fc) {
        if (fc.fileName.endsWith("Controller")) {
            FILE_CACHE_CONTROLLER.put(StrUtil.removeSuffix(fc.filePath, ".java"), fc);
            return;
        }
        FILE_CACHE.put(fc.fileName, fc);
    }


    @Builder
    @ToString
    @Getter
    public static class FC {

        private final String fileName;

        private final String filePath;
    }
}

