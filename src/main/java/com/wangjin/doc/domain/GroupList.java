package com.wangjin.doc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupList {

    @SerializedName("childGroupList")
    private List<GroupList> childGroupList;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("groupID")
    private String groupID;

    @SerializedName("isChild")
    private int isChild;
}
