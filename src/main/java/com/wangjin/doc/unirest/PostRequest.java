package com.wangjin.doc.unirest;

import lombok.SneakyThrows;

import java.io.OutputStream;
import java.net.HttpURLConnection;

public class PostRequest extends BaseRequest<PostRequest> {
    public PostRequest(String url) {
        super(url, HttpMethod.POST);
    }


    @SneakyThrows
    public PostRequest body(String body) {
        HttpURLConnection httpURLConnection = HTTP_URL_CONNECTION_THREAD_LOCAL.get();
        // 发送POST请求必须设置允许输出
        httpURLConnection.setDoOutput(true);
        // 发送POST请求必须设置允许输入
        httpURLConnection.setDoInput(true);
        OutputStream os = httpURLConnection.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();
        return this;
    }
}
