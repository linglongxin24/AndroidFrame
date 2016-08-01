package com.kejiang.yuandl.base;

import org.xutils.http.RequestParams;

/**
 * Created by yuandl on 2016/7/26 0026.
 */
public interface HttpRequest {
    /**
     * 同时只有一个请求
     *
     * @param requestParams 请求的参数
     */
    void ajax(RequestParams requestParams);

    /**
     * 同时有多个请求
     *
     * @param requestParams 请求的参数
     * @param requestCode   每次请求的请求码
     */
    void ajax(RequestParams requestParams, int requestCode);
}
