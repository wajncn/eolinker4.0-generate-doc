package com.wangjin.doc.unirest.request;

public interface HttpRequest<R extends HttpRequest> {
    R header(String name, String value);

    R asString();

    String getBody();
}
