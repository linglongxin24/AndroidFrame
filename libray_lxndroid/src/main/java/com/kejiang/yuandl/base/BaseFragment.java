package com.kejiang.yuandl.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.kejiang.yuandl.app.MyApplication;
import com.kejiang.yuandl.bean.JsonBean;
import com.kejiang.yuandl.utils.CheckNetwork;
import com.kejiang.yuandl.utils.Tools;
import com.kejiang.yuandl.view.LoadingDialog;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.RefWatcher;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * com.bm.falvzixun.fragment.BaseFragment
 *
 * @author yuandl on 2016/1/15.
 *         描述主要干什么
 */
public class BaseFragment extends Fragment  {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Callback.Cancelable cancelable;
    /**
     * 加载数据对话框
     */
    public LoadingDialog loadingDialog;

    /**
     * 异步网络请求类
     *
     * @param requestParams
     */
    protected void ajax(RequestParams requestParams) {
        if (!CheckNetwork.isNetworkAvailable(getContext())) {
            showToast("网络不可用，请检查网络连接！");
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
        }
        if (requestParams != null) {
            String uri = requestParams.getUri();
            if (!uri.isEmpty()) {
                String[] split = uri.split("/");
//                Logger.d("split=" + Arrays.toString(split));
                String method = split[split.length - 1].substring(0, split[split.length - 1].indexOf("."));
                String mode = split[split.length - 2];
                String sign = MD5.md5(mode + method);
                requestParams.addBodyParameter("sign", sign);
            }

        }
        Logger.d("url=" + requestParams.getUri() + "\nrequestParams=" + requestParams.getStringParams().toString());
        List<KeyValue> params = requestParams.getStringParams();
        for (KeyValue keyValue : params) {
            if (keyValue.key.contains(":")) {
                throw new RuntimeException("参数异常！");
            }
        }
        //                    jsonBean = JSON.parseObject(result, JsonBean.class);
        cancelable = x.http().post(requestParams, new Callback.ProgressCallback<String>() {

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
//                    jsonBean = JSON.parseObject(result, JsonBean.class);
                    jsonBean = jsonParse(result);
                    if (jsonBean.getMsg() != null && !jsonBean.getMsg().isEmpty()) {
                        showToast(jsonBean.getMsg());
                    }
                    if (jsonBean.getCode() == 200) {
                        Map<String, Object> data = new ArrayMap<String, Object>();
                        if (null != jsonBean.getData() && jsonBean.getData().size() > 0) {
                            data = jsonBean.getData();
                        }
                        netOnSuccess(data);
                    } else {

                        netOnOtherStates(jsonBean.getCode(), jsonBean.getMsg());
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
            public void onCancelled(CancelledException cex) {
                Logger.d("用户取消了访问网络....");
                netOnCancelled();
            }

            @Override
            public void onFinished() {
                netOnFinish();
            }

        });
    }
//
//    private JsonBean jsonParse(String json) throws JSONException {
//        Map<String, Object> map = JSON.parseObject(json, new TypeToken<Map<String, Object>>() {
//        }.getType());
//        JsonBean jsonBean = new JsonBean();
//        if (map.containsKey("data")) {
//            Object data = map.get("data");
//            System.out.println("data.getClass().getName()=" + data.getClass().getName());
//            Map<String, Object> rrData = null;
//            if (data instanceof String) {
//                System.out.println("data instanceof String");
//                rrData = new HashMap<>();
//                rrData.put("data", data.toString());
//            } else if (data instanceof JSONArray) {
//                System.out.println("data instanceof JSONArray");
//                rrData = new HashMap<>();
//                rrData.put("data", data);
//            } else if (data instanceof com.alibaba.fastjson.JSONObject) {
//                System.out.println("data instanceof JSONObject");
//                rrData = (Map) data;
//            }
//            jsonBean.setData(rrData);
//        }
//        jsonBean.setCode(Integer.valueOf(map.get("code").toString()));
//        jsonBean.setMsg(Tools.getValue(map, "msg"));
//
//        return jsonBean;
//    }

    private JsonBean jsonParse(String json) throws JSONException {
        ArrayMap<String,Object> arrayMap= JSON.parseObject(json,new TypeReference<ArrayMap<String,Object>>(){
        }.getType());
        JsonBean jsonBean = new JsonBean();
        if (arrayMap.containsKey("data")) {
            Object data = arrayMap.get("data");
            System.out.println("data.getClass().getName()=" + data.getClass().getName());
            ArrayMap<String,Object> rrData = null;
            if (data instanceof String) {
                System.out.println("data instanceof String");
                rrData = new ArrayMap<String,Object>();
                rrData.put("data", data.toString());
            } else if (data instanceof JSONArray) {
                System.out.println("data instanceof JSONArray");
                rrData = new ArrayMap<String,Object>();
                rrData.put("data", data);
            } else if (data instanceof com.alibaba.fastjson.JSONObject) {
                System.out.println("data instanceof JSONObject");
                rrData = JSON.parseObject(data.toString(),new TypeReference<ArrayMap<String,Object>>(){
                }.getType());
            }
            jsonBean.setData(rrData);
        }
        jsonBean.setCode(Integer.valueOf(arrayMap.get("code").toString()));
        jsonBean.setMsg(Tools.getValue(arrayMap, "msg"));

        return jsonBean;
    }
    /**
     * 开始访问网络
     */
    public void netOnStart() {
        loadingDialog.show("正在请求网络...");
    }

    /**
     * 访问网络的进程
     */
    public void netOnLoading(long total, long current, boolean isUploading) {
    }

    /**
     * 访问网络成功
     */
    public void netOnSuccess(Map<String, Object> data) {

    }

    /**
     * 访问网络成功的其他状态
     */
    public void netOnOtherStates(int code, String msg) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 访问网络结束
     */
    public void netOnFinish() {
        loadingDialog.dismiss();
    }

    /**
     * 访问网络失败
     */
    public void netOnFailure(Throwable ex) {
        Logger.d(ex.getMessage());
        if (ex instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) ex;
            int responseCode = httpEx.getCode();
            String responseMsg = httpEx.getMessage();
            String errorResult = httpEx.getResult();
            Toast.makeText(x.app(), "网络错误：" + ex.getMessage(), Toast.LENGTH_LONG).show();
            // ...
        } else { // 其他错误
            Toast.makeText(x.app(), "连接服务器失败，请稍后再试！ex=" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            // ...
        }


    }

    /**
     * 取消访问网络
     */
    public void netOnCancelled() {
    }


    //    /**
//     * 弹出Toast便捷方法
//     *
//     * @param charSequence
//     */
//    public void showToast(CharSequence charSequence) {
//        Toast.makeText(getContext(), charSequence, Toast.LENGTH_SHORT).show();
//
//    }
    private Toast toast;

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

    @Override
    public void onPause() {
        super.onPause();
        if (null != toast) {
            toast.cancel();
        }

    }

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    protected boolean isLoadData = false;
    protected boolean isPrepared = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }


    /**
     * 不可见
     */
    protected void onInvisible() {


    }


    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected void lazyLoad() {

    }

    protected void setEmptyView(ListView listView) {
        TextView emptyView = new TextView(getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        listView.setVisibility(View.VISIBLE);
    }

    protected void setEmptyView(ListView listView, String text) {
        TextView emptyView = new TextView(getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText(text);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
