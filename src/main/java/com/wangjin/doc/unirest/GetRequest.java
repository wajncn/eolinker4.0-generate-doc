package com.wangjin.doc.unirest;

public class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest(String url) {
        super(url, HttpMethod.GET);
    }
}
