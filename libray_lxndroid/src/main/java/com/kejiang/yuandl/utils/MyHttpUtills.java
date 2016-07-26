package com.kejiang.yuandl.utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.kejiang.yuandl.bean.JsonBean;
import com.kejiang.yuandl.view.LoadingDialog;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuandl on 2016/6/27 0027.
 */
public class MyHttpUtills {

    private Callback.Cancelable cancelable;

    private LoadingDialog loadingDialog;

    private Toast toast;

    private Context context;

    private MyHttpCallback myHttpCallback;

    public MyHttpUtills(Context context,MyHttpCallback myHttpCallback) {
        this.context = context;
        this.myHttpCallback = myHttpCallback;
    }

    /**
     * 异步网络请求类
     *
     * @param requestParams
     */
    public void ajax(RequestParams requestParams) {
        ajax(requestParams, 0);
    }

    /**
     * 异步网络请求类
     *
     * @param requestParams
     * @param requestCode   区分不同的网络请求
     */
    public void ajax(RequestParams requestParams, int requestCode) {
        if (!CheckNetwork.isNetworkAvailable(context)) {
            showToast("网络不可用，请检查网络连接！");
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context);
        }
        SharedPreferencesUtils sp = new SharedPreferencesUtils(x.app());
        boolean isLogin = (boolean) sp.getParam("login", false);
        if (isLogin) {
            requestParams.addBodyParameter("mId", (String) sp.getParam("mId", ""));
        }
        boolean hasLocation = (boolean) sp.getParam("hasLocation", false);
        if (hasLocation) {
            requestParams.addBodyParameter("lng", (String) sp.getParam("lng", ""));
            requestParams.addBodyParameter("lat", (String) sp.getParam("lat", ""));
        }
        Logger.d("url=" + requestParams.getUri() + "\nrequestParams=" + requestParams.getStringParams().toString());
        List<KeyValue> params = requestParams.getStringParams();
        for (KeyValue keyValue : params) {
            if (keyValue.key.contains(":")) {
                throw new RuntimeException("参数异常！");
            }
        }
        cancelable = x.http().post(requestParams, new MyCallback(requestCode));
    }

    private class MyCallback implements Callback.ProgressCallback<String> {
        private int requestCode = 0;

        public MyCallback(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onWaiting() {
        }

        @Override

        public void onStarted() {
            netOnStart();
        }

        @Override
        public void onLoading(long total, long current, boolean isDownloading) {
            netOnLoading(total, current, isDownloading);
        }

        @Override
        public void onSuccess(String result) {
            Logger.json(result);
            JsonBean jsonBean = null;
            try {
                jsonBean = jsonParse(result);
                if (jsonBean.getMsg() != null && !jsonBean.getMsg().isEmpty()) {
                    showToast(jsonBean.getMsg());
                }
                if (jsonBean.getStatus() == 1) {
                    Map<String, Object> data = new ArrayMap<String, Object>();
                    if (null != jsonBean.getData() && jsonBean.getData().size() > 0) {
                        data = jsonBean.getData();
                    }
                    netOnSuccess(data, requestCode);
                } else {
                    netOnOtherStates(jsonBean.getStatus(), jsonBean.getMsg(), requestCode);
                }
            } catch (Exception e) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Logger.d(result);
                showToast("服务器异常！");
                e.printStackTrace();
            } finally {
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            netOnFailure(ex);

        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            Logger.d("用户取消了访问网络....");
            netOnCancelled();
        }

        @Override
        public void onFinished() {
            netOnFinish(requestCode);
        }


    }

    private JsonBean jsonParse(String json) throws JSONException {
        ArrayMap<String, Object> arrayMap = JSON.parseObject(json, new TypeReference<ArrayMap<String, Object>>() {
        }.getType());
        JsonBean jsonBean = new JsonBean();
        if (arrayMap.containsKey("data")) {
            Object data = arrayMap.get("data");
            System.out.println("data.getClass().getName()=" + data.getClass().getName());
            ArrayMap<String, Object> rrData = null;
            if (data instanceof String) {
                System.out.println("data instanceof String");
                rrData = new ArrayMap<String, Object>();
                rrData.put("data", data.toString());
            } else if (data instanceof JSONArray) {
                System.out.println("data instanceof JSONArray");
                rrData = new ArrayMap<String, Object>();
                rrData.put("data", data);
            } else if (data instanceof com.alibaba.fastjson.JSONObject) {
                System.out.println("data instanceof JSONObject");
                rrData = JSON.parseObject(data.toString(), new TypeReference<ArrayMap<String, Object>>() {
                }.getType());
            }
            jsonBean.setData(rrData);
        } else {
            Set<String> keys = arrayMap.keySet();
            ArrayMap<String, Object> rrData = new ArrayMap<>();
            for (String s : keys) {
                if (!s.equals("status") && !s.equals("msg")) {
                    rrData.put(s, arrayMap.get(s));
                }
            }
            jsonBean.setData(rrData);
        }
        jsonBean.setStatus(Integer.valueOf(arrayMap.get("status").toString()));
        jsonBean.setMsg(Tools.getValue(arrayMap, "msg"));

        return jsonBean;
    }

    /**
     * 开始访问网络
     */
    protected void netOnStart() {
        loadingDialog.show("Loading...");
        myHttpCallback.netOnStart();
    }

    /**
     * 访问网络的进程
     */
    protected void netOnLoading(long total, long current, boolean isUploading) {
    }

    /**
     * 访问网络成功
     */
    protected void netOnSuccess(Map<String, Object> data, int requestCode) {
        netOnSuccess(data);
        myHttpCallback.netOnSuccess(data,requestCode);
    }

    /**
     * 访问网络成功
     */
    protected void netOnSuccess(Map<String, Object> data) {
        myHttpCallback.netOnSuccess(data);
    }

    /**
     * 访问网络成功的其他状态
     */
    protected void netOnOtherStates(int status, String msg) {
        myHttpCallback.netOnOtherStatus(status, msg);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 访问网络成功的其他状态
     */
    protected void netOnOtherStates(int status, String msg, int requestCode) {
        netOnOtherStates(status, msg);
        myHttpCallback.netOnOtherStatus(status, msg, requestCode);
    }

    /**
     * 访问网络结束
     */
    protected void netOnFinish() {
        loadingDialog.dismiss();
        myHttpCallback.netOnFinish();
    }

    /**
     * 访问网络结束
     */
    protected void netOnFinish(int requestCode) {
        loadingDialog.dismiss();
        netOnFinish();
    }

    /**
     * 访问网络失败
     */
    protected void netOnFailure(Throwable ex) {
        Logger.d(ex.getMessage());
        if (ex instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) ex;
            int responseCode = httpEx.getCode();
            String responseMsg = httpEx.getMessage();
            String errorResult = httpEx.getResult();
            Toast.makeText(x.app(), "网络错误：" + ex.getMessage(), Toast.LENGTH_LONG).show();
            // ...
        } else if (ex instanceof SocketTimeoutException) {
            Toast.makeText(x.app(), "连接服务器超时", Toast.LENGTH_LONG).show();
        } else { // 其他错误
            Toast.makeText(x.app(), "连接服务器失败，请稍后再试！ex=" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            // ...
        }
        myHttpCallback.netOnFailure(ex);
        myHttpCallback.netOnFinish();

    }

    /**
     * 取消访问网络
     */
    protected void netOnCancelled() {
    }

    /**
     * 弹出Toast便捷方法
     *
     * @param charSequence
     */
    public void showToast(CharSequence charSequence) {
        if (null == toast) {
            toast = Toast.makeText(x.app(), charSequence, Toast.LENGTH_SHORT);
        } else {
            toast.setText(charSequence);
        }
        toast.show();

    }
}
