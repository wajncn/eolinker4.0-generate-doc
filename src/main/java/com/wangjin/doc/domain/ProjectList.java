package com.wangjin.doc.domain;


import cn.hutool.core.annotation.Alias;
import lombok.Data;


/**
 * @program: gen-interfacedoc
 * @description
 * @author: 王进
 **/
@Data
public class ProjectList {


    /**
     * projectType : 0
     * userType : 3
     * projectName : 亿品
     * projectID : 299
     * projectUpdateTime : 2020-05-11 13:59:59
     * projectVersion : 1.0
     */

    @Alias("projectType")
    private int projectType;

    @Alias("userType")
    private int userType;

    @Alias("projectName")
    private String projectName;

    @Alias("projectID")
    private int projectID;

    @Alias("projectUpdateTime")
    private String projectUpdateTime;

    @Alias("projectVersion")
    private String projectVersion;


}
