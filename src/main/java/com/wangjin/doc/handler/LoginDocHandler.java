package com.wangjin.doc.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.ApiList;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.domain.GroupList;
import com.wangjin.doc.domain.ProjectList;
import com.wangjin.doc.utils.BaseUtils;
import kong.unirest.Unirest;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

import static com.wangjin.doc.utils.BaseUtils.getMD5Str;

/**
 * @description:
 * @author: wajn
 * @create: 2020-04-25 19:32
 **/
public class LoginDocHandler {
    private static final String SUCCESS = "000000";
    private static Map<String, ApiList> apiLists = new HashMap<>();
    private static String token = null;
    private static final Gson GSON = new Gson();

    public static void login(@NonNull String username, @NonNull String password) {
        try {
            String body = "loginName=" + username + "&loginPassword=" + getMD5Str(password);
            String response = Unirest.post("https://doc.f.wmeimob.com/Guest/login")
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .body(body)
                    .asString().getBody();

            if (!Application.LICENSE_STATUS) {
                System.exit(1);
            }

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
                throw new IllegalArgumentException("账号密码错误,无法自动同步到文档系统");
            }
            token = jsonObject.get("JSESSIONID").getAsString();
            apiLists = LoginDocHandler.getAllApiList().stream().collect(Collectors.toMap(k -> k.getApiURI() + k.getApiRequestType(), v -> v, (v1, v2) -> v1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshApiList() {
        apiLists = LoginDocHandler.getAllApiList().stream().collect(Collectors.toMap(k -> k.getApiURI() + k.getApiRequestType(), v -> v, (v1, v2) -> v1));
    }


    public static void main(String[] args) {
        token = "21E17E2E206DC057879743030FC9A2C4";
        System.out.println(getGroupList());
        System.out.println(getGroupListForMap());
    }


    @SneakyThrows
    public static LinkedHashMap<String, String> getGroupListForMap() {
        List<GroupList> groupLists = getGroupList();
        List<GroupList> list = new ArrayList<>(groupLists.size());

        for (GroupList groupList : groupLists) {
            list.add(groupList);
            List<GroupList> childGroupList = groupList.getChildGroupList();
            if (!childGroupList.isEmpty()) {
                childGroupList.forEach(c -> {
                    c.setGroupName("    " + c.getGroupName());
                    list.add(c);

                    List<GroupList> childGroupList2 = c.getChildGroupList();

                    childGroupList2.forEach(c2 -> {
                        c2.setGroupName("        " + c2.getGroupName());
                        list.add(c2);
                    });
                });
            }
        }

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        list.forEach(a -> {
            String id = map.get(a.getGroupName());
            if (id == null) {
                map.put(a.getGroupName(), a.getGroupID());
            } else {
                map.put(a.getGroupName() + "(2)", a.getGroupID());
            }
        });
        return map;
    }


    @SneakyThrows
    public static List<GroupList> getGroupList() {
        String body = "projectID=" + DocConfig.get().getProjectId() + "&groupID=-1&childGroupID=-1";

        String response = Unirest.post("https://doc.f.wmeimob.com/Group/getGroupList")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            return new ArrayList<>();
        }
        List<GroupList> list = GSON.fromJson(jsonObject.get("groupList").getAsJsonArray(), new TypeToken<List<GroupList>>() {
        }.getType());

        return list;
    }


    @SneakyThrows
    public static List<ProjectList> getProjectList() {
        DocConfig docConfig = DocConfig.get();
        if (!docConfig.isSynchronous()) {
            BaseUtils.print("未开启同步,不自动同步文档系统");
            return new ArrayList<>();
        }
        String body = "projectType=-1";

        String response = Unirest.post("https://doc.f.wmeimob.com/Project/getProjectList")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            return new ArrayList<>();
        }
        List<ProjectList> list = GSON.fromJson(jsonObject.get("projectList").getAsJsonArray(), new TypeToken<List<ProjectList>>() {
        }.getType());
        return list;
    }


    @SneakyThrows
    public static List<ApiList> getAllApiList() {
        DocConfig docConfig = DocConfig.get();
        if (!docConfig.isSynchronous()) {
            BaseUtils.print("未开启同步,不自动同步文档系统");
            return new ArrayList<>();
        }

        String body = "projectID=" + docConfig.getProjectId() + "&groupID=" + docConfig.getGroupId() + "&orderBy=3&asc=0";

        String response = Unirest.post("https://doc.f.wmeimob.com/Api/getApiList")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            return new ArrayList<>();
        }
        List<ApiList> list = GSON.fromJson(jsonObject.get("apiList").getAsJsonArray(), new TypeToken<List<ApiList>>() {
        }.getType());
        return list;
    }


    @SneakyThrows
    private static void del(String id) {
        if (id == null) {
            return;
        }
        DocConfig docConfig = DocConfig.get();
        if (!docConfig.isSynchronous()) {
            BaseUtils.print("未开启同步,不自动同步文档系统");
            return;
        }

        String body = "projectID=" + docConfig.getProjectId() + "&apiID=" + URLUtil.encode("[".concat(id).concat("]"));

        String response = Unirest.post("https://doc.f.wmeimob.com/Api/removeApi")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            BaseUtils.printError("修改失败,未找到");
            return;
        }
    }

    @SneakyThrows
    public static void upload(@NonNull String data, InterfaceDoc.MethodDoc doc) {
        DocConfig docConfig = DocConfig.get();
        if (!docConfig.isSynchronous()) {
            BaseUtils.print("未开启同步,不自动同步文档系统");
            return;
        }


        if (!Project.LICENSE_STATUS) {
            System.exit(1);
        }


        ApiList apiList = apiLists.get(doc.getRequestMapping() + doc.getMethodType().getApiRequestType());
        if (apiList != null) {
            if (docConfig.isUpdate()) {
                BaseUtils.printTips("文档{}已存在, 系统正在修改文档...", doc.getRequestMapping());
                del(apiList.getApiID());
            } else {
                BaseUtils.printTips("文档{}已存在, 系统已忽略更新", doc.getRequestMapping());
                return;
            }
        }

        String body = "projectID=" + docConfig.getProjectId() + "&groupID=" + StrUtil.blankToDefault(docConfig.getGroupId(), "0") + "&data=" + URLUtil.encode(data);

        String response = Unirest.post("https://doc.f.wmeimob.com/Api/importApi")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            BaseUtils.printError("令牌无效,无法自动同步到文档系统 jsonObject:{}     token:{}    body:{}", jsonObject, token, body);
            return;
        }
    }
}