package com.wangjin.doc.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wangjin.doc.base.Application;
import com.wangjin.doc.base.DocConfig;
import com.wangjin.doc.base.InterfaceDoc;
import com.wangjin.doc.base.Project;
import com.wangjin.doc.domain.ApiList;
import com.wangjin.doc.domain.GroupList;
import com.wangjin.doc.domain.ProjectList;
import com.wangjin.doc.unirest.Unirest;
import com.wangjin.doc.util.BaseUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wangjin.doc.util.BaseUtils.getMD5Str;

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

    public static void login() {
        DocConfig docConfig = DocConfig.get();
        String body = "loginName=" + docConfig.getUsername() + "&loginPassword=" + getMD5Str(docConfig.getPassword());


        String response = Unirest.post("https://doc.f.wmeimob.com/Guest/login")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .body(body)
                .asString().getBody();

        if (!Application.LICENSE_STATUS) {
            return;
        }

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!SUCCESS.equals(jsonObject.get("statusCode").getAsString())) {
            throw new IllegalArgumentException("账号密码错误,无法自动同步到文档系统");
        }
        token = jsonObject.get("JSESSIONID").getAsString();
        apiLists = LoginDocHandler.getAllApiList().stream().collect(Collectors.toMap(k -> k.getApiURI() + k.getApiRequestType(), v -> v, (v1, v2) -> v1));
    }

    public static void refreshApiList() {
        apiLists = LoginDocHandler.getAllApiList().stream().collect(Collectors.toMap(k -> k.getApiURI() + k.getApiRequestType(), v -> v, (v1, v2) -> v1));
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
        return parseList(jsonObject.get("groupList").getAsJsonArray(), GroupList.class);
    }


    private static <T> List<T> parseList(JsonArray array, Class<T> c) {
        List<T> list = new ArrayList<>();
        array.forEach(a -> {
            list.add(GSON.fromJson(a.getAsJsonObject(), c));
        });
        return list;
    }


    @SneakyThrows
    public static List<ProjectList> getProjectList() {
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
        return parseList(jsonObject.get("projectList").getAsJsonArray(), ProjectList.class);
    }


    @SneakyThrows
    public static List<ApiList> getAllApiList() {
        DocConfig docConfig = DocConfig.get();

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

        return parseList(jsonObject.get("apiList").getAsJsonArray(), ApiList.class);
    }


    @SneakyThrows
    private static void del(String id) {
        if (id == null) {
            return;
        }
        DocConfig docConfig = DocConfig.get();

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


    private static final LinkedBlockingQueue<Upload> UPLOAD_DATA = new LinkedBlockingQueue<>(1024);
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 24, 1,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(4096), new ThreadFactoryBuilder()
            .setNameFormat("UPLOAD_DATA-pool-%d").build());

    @Builder
    @Getter
    public static class Upload {
        private final String data;
        private final InterfaceDoc.MethodDoc doc;
    }


    /**
     * 上传文档 加入队列中
     *
     * @param data
     * @param doc
     */
    public static void addQueue(String data, InterfaceDoc.MethodDoc doc) {
        UPLOAD_DATA.add(Upload.builder().data(data).doc(doc).build());
    }


    @SneakyThrows
    private static void upload(String data, InterfaceDoc.MethodDoc doc) {
        DocConfig docConfig = DocConfig.get();

        if (!Project.LICENSE_STATUS) {
            return;
        }


        ApiList apiList = apiLists.get(doc.getRequestMapping() + doc.getMethodType().getApiRequestType());
        if (apiList != null) {
            if (docConfig.isUpdate()) {
                BaseUtils.printTips("文档 {} {} 已存在, 系统正在修改文档...", doc.getRequestMapping(), doc.getMethodType());
                del(apiList.getApiID());
            } else {
                BaseUtils.printTips("文档 {} {} 已存在, 系统已忽略更新", doc.getRequestMapping(), doc.getMethodType());
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

    public static class UploadDoc implements Runnable {

        @Setter
        private boolean execute = true;


        @SneakyThrows
        @Override
        public void run() {
            BaseUtils.printTips("监听队列子线程已启动...");
            while (true) {
                if (!execute) {
                    if (UPLOAD_DATA.isEmpty() && threadPoolExecutor.getActiveCount() == 0) {
                        BaseUtils.printTips("队列为空,监听子线程销毁中...");
                        return;
                    }
                }
                final Upload poll = UPLOAD_DATA.poll();
                if (poll == null) {
                    continue;
                }
                threadPoolExecutor.execute(() -> {
                    try {
                        upload(poll.getData(), poll.getDoc());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}