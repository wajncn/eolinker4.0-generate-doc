package com.wangjin.doc.cache;

import cn.hutool.core.util.StrUtil;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.Constant;
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

    /**
     * 存放所有文件的controller, 根据模块分组
     */
    private static final Map<String, FC> MODULE_CACHE = new HashMap<>();


    /**
     * 存放所有文件的map
     */
    private static final Map<String, FC> FILE_CACHE = new HashMap<>();


    /**
     * 存放controller的map
     */
    private static final Map<String, FC> FILE_CACHE_CONTROLLER = new HashMap<>();


    public static FC getFc(String fileName) {
        if (!Constant.LICENSE_STATUS) {
            return null;
        }
        //有限从当前模块缓存拿, 拿不到的在从总的缓存取
        return MODULE_CACHE.getOrDefault(Project.module + fileName, FILE_CACHE.get(fileName));
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
                return;
            }
            return;
        }
        MODULE_CACHE.put(fc.getModule() + fc.fileName, fc);
        FILE_CACHE.put(fc.fileName, fc);
    }


    @Builder
    @ToString
    @Getter
    public static class FC {

        /**
         * 模块名字, 为了解决多模块同包重名问题
         */
        private final String module;

        private final String fileName;

        private final String filePath;
    }
}

