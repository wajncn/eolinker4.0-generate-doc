package com.wangjin.doc.domain;

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

    private int projectType;
    private int userType;
    private String projectName;
    private int projectID;
    private String projectUpdateTime;
    private String projectVersion;

}
