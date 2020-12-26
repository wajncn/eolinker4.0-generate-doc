package com.wangjin.doc.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.domain.ApiList;
import com.wangjin.doc.domain.DocConfig;
import com.wangjin.doc.domain.ProjectList;
import com.wangjin.doc.utils.BaseUtils;
import kong.unirest.Unirest;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @SneakyThrows
    public static void login(@NonNull String username, @NonNull String password) {
        String body = "loginName=" + username + "&loginPassword=" + getMD5Str(password);
        String response = Unirest.post("https://doc.f.wmeimob.com/Guest/login")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .body(body)
                .asString().getBody();

        JSONObject jsonObject = JSONUtil.parseObj(response);
        if (!SUCCESS.equals(jsonObject.getStr("statusCode"))) {
            throw new IllegalArgumentException("账号密码错误,无法自动同步到文档系统");
        }
        token = jsonObject.getStr("JSESSIONID");
        apiLists = LoginDocHandler.getAllApiList().stream().collect(Collectors.toMap(k -> k.getApiName() + k.getApiURI(), v -> v, (v1, v2) -> v1));
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

        JSONObject jsonObject = JSONUtil.parseObj(response);
        if (!SUCCESS.equals(jsonObject.getStr("statusCode"))) {
            return new ArrayList<>();
        }
        JSONArray apiList = jsonObject.getJSONArray("projectList");
        return apiList.toList(ProjectList.class);
    }


    @SneakyThrows
    private static List<ApiList> getAllApiList() {
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

        JSONObject jsonObject = JSONUtil.parseObj(response);
        if (!SUCCESS.equals(jsonObject.getStr("statusCode"))) {
            return new ArrayList<>();
        }
        JSONArray apiList = jsonObject.getJSONArray("apiList");
        return apiList.toList(ApiList.class);
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

        JSONObject jsonObject = JSONUtil.parseObj(response);
        if (!SUCCESS.equals(jsonObject.getStr("statusCode"))) {
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

        ApiList apiList = apiLists.get(doc.getComment() + doc.getRequestMapping());
        if (apiList != null) {
            BaseUtils.printTips("文档{}已存在, 系统正在修改文档...", doc.getComment());
            del(apiList.getApiID());
        }

        String body = "projectID=" + docConfig.getProjectId() + "&groupID=" + StrUtil.blankToDefault(docConfig.getGroupId(), "0") + "&data=" + URLUtil.encode(data);

        String response = Unirest.post("https://doc.f.wmeimob.com/Api/importApi")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Cookie", "JSESSIONID=" + token)
                .body(body)
                .asString().getBody();


        JSONObject jsonObject = JSONUtil.parseObj(response);

        if (!SUCCESS.equals(jsonObject.getStr("statusCode"))) {
            BaseUtils.printError("令牌无效,无法自动同步到文档系统");
            return;
        }
    }
}