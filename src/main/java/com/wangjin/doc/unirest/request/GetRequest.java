package com.wangjin.doc.unirest.request;

public class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest(String url) {
        super(url, HttpMethod.GET);
    }
}
