package com.wangjin.doc.unirest.request;

import cn.hutool.core.io.IoUtil;
import com.wangjin.doc.util.BaseUtils;
import lombok.SneakyThrows;

import java.net.HttpURLConnection;
import java.net.URL;

abstract class BaseRequest<R extends HttpRequest> implements HttpRequest<R> {

    static final ThreadLocal<HttpURLConnection> HTTP_URL_CONNECTION_THREAD_LOCAL = new ThreadLocal<>();

    public BaseRequest(String url, HttpMethod method) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            HTTP_URL_CONNECTION_THREAD_LOCAL.set(httpURLConnection);
        } catch (Exception e) {
            BaseUtils.printError("Exception:{}", e);
        }
    }


    @Override
    public R header(String name, String value) {
        HTTP_URL_CONNECTION_THREAD_LOCAL.get().setRequestProperty(name, value);
        return (R) this;
    }


    @Override
    public R asString() {
        return (R) this;
    }

    @Override
    @SneakyThrows
    public String getBody() {
        try {
            return IoUtil.read(HTTP_URL_CONNECTION_THREAD_LOCAL.get().getInputStream(), "utf-8");
        } finally {
            HTTP_URL_CONNECTION_THREAD_LOCAL.remove();
        }
    }
}
