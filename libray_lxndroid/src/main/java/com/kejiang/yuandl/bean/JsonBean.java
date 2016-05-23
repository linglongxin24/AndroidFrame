package com.kejiang.yuandl.bean;

import java.util.Map;

/**
 * com.bm.falvzixun.bean.GsonBean
 *
 * @author yuandl on 2016/1/4.
 *         json解析工具类对象
 */
public class JsonBean {
    private int code;
    private String msg;
    private Map<String, Object> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
