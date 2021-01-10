package com.wangjin.doc.cache;

import cn.hutool.core.util.StrUtil;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Project;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;


/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
@NoArgsConstructor
public final class FileCache {

    private static final Map<String, FC> FILE_CACHE = new HashMap<>();


    private static final Map<String, FC> FILE_CACHE_CONTROLLER = new HashMap<>();


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
        if (!Application.LICENSE_STATUS) {
            System.exit(1);
        }
        FILE_CACHE.clear();
        FILE_CACHE_CONTROLLER.clear();
    }


    public static void addFc(FC fc) {
        if (fc.fileName.endsWith("Controller")) {
            FILE_CACHE_CONTROLLER.put(StrUtil.removeSuffix(fc.filePath, ".java"), fc);
            if (!Project.LICENSE_STATUS) {
                System.exit(1);
            }
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

