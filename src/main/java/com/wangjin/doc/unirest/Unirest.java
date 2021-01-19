package com.wangjin.doc.unirest;

import com.wangjin.doc.unirest.request.GetRequest;
import com.wangjin.doc.unirest.request.HttpRequest;
import com.wangjin.doc.unirest.request.PostRequest;
import lombok.SneakyThrows;

public class Unirest {

    @SneakyThrows
    public static HttpRequest<GetRequest> get(String url) {
        return new GetRequest(url);
    }

    @SneakyThrows
    public static HttpRequest<PostRequest> post(String url) {
        return new PostRequest(url);
    }
}
