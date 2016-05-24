package com.kejiang.yuandl.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.kejiang.yuandl.R;
import com.kejiang.yuandl.bean.JsonBean;
import com.kejiang.yuandl.utils.AppManager;
import com.kejiang.yuandl.utils.CheckNetwork;
import com.kejiang.yuandl.utils.Tools;
import com.kejiang.yuandl.view.LoadingDialog;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.MD5;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * com.bm.falvzixun.activities.BaseActivity;
 *
 * @author yuandl on 2015/12/17.
 *         所有页面的基类
 */
public abstract class BaseActivity extends AutoLayoutActivity implements View.OnClickListener {
    private TextView mTitleTextView;
    private ImageView mBackwardbButton;
    private TextView mForwardButton;
    private FrameLayout mContentLayout;
    private LinearLayout llRoot;
    private LinearLayout layout_titlebar;
    protected Context context;
    Dialog dialog;
    /**
     * 加载数据对话框
     */
    public LoadingDialog loadingDialog;
    private Callback.Cancelable cancelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
//        setImmersionStatus();
        setupViews();
        context = this;
        initTitleBar();
        initViews();
        initData();
        addListener();
    }

    public TextView getmForwardButton() {
        return mForwardButton;
    }

    public ImageView getmBackwardbButton() {
        return mBackwardbButton;
    }

    /**
     * 初始化设置标题栏
     */
    public abstract void initTitleBar();

    /**
     * 初始化view控件
     */
    public abstract void initViews();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 给view添加事件监听
     */
    public abstract void addListener();

    /**
     * 设置沉浸式状态栏
     */
    private void setImmersionStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 加载 activity_title 布局 ，并获取标题及两侧按钮
     */
    private void setupViews() {
        super.setContentView(R.layout.ac_title);
        llRoot = (LinearLayout) findViewById(R.id.llRoot);
        AutoUtils.auto(llRoot);
        layout_titlebar = (LinearLayout) findViewById(R.id.layout_titlebar);
        mTitleTextView = (TextView) findViewById(R.id.text_title);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mBackwardbButton = (ImageView) findViewById(R.id.button_backward);
        mForwardButton = (TextView) findViewById(R.id.button_forward);
    }

    /**
     * 设置标题栏是否可见
     *
     * @param visibility
     */
    public void setTitleBarVisible(int visibility) {
        layout_titlebar.setVisibility(visibility);
    }

    /**
     * 设置标题栏的整体背景颜色（含沉浸式状态栏）
     *
     * @param color
     */
    public void setTitleBarBackground(int color) {
        setStatusBarBackground(color);
        layout_titlebar.setBackgroundColor(color);
    }

    /**
     * 设置返回按钮背景图片（含沉浸式状态栏）
     *
     * @param drawable
     */
    public void setBackwardButtonBackgroundDrawable(Drawable drawable, LinearLayout.LayoutParams layoutParams) {
        mBackwardbButton.setImageDrawable(drawable);
        mBackwardbButton.setLayoutParams(layoutParams);
        AutoUtils.auto(mBackwardbButton);
    }

    /**
     * 设置返回按钮背景图片（含沉浸式状态栏）
     *
     * @param drawable
     */
    public void setBackwardButtonBackgroundDrawable(Drawable drawable) {
        mBackwardbButton.setImageDrawable(drawable);
    }

    /**
     * 设置浸式状态栏的整体背景颜色
     *
     * @param color
     */
    public void setStatusBarBackground(int color) {
        llRoot.setBackgroundColor(color);
    }

    /**
     * 设置浸式状态栏的整体背景图片(慎用)
     *
     * @param drawable
     */
    public void setStatusBarBackgroundDrawable(Drawable drawable) {
        llRoot.setBackgroundDrawable(drawable);
    }

    /**
     * 是否显示返回按钮
     *
     * @param backwardResid 文字
     * @param show          true则显示
     */
    protected void showBackwardView(int backwardResid, boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
//                mBackwardbButton.setText(backwardResid);
                mBackwardbButton.setVisibility(View.VISIBLE);
            } else {
                mBackwardbButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    protected void setBackwardViewLayoutParams(LinearLayout.LayoutParams layoutParams) {
        if (mBackwardbButton != null) {
            mBackwardbButton.setLayoutParams(layoutParams);
        }
    }

    protected void setForwardViewLayoutParams(LinearLayout.LayoutParams layoutParams) {
        if (mForwardButton != null) {
            mForwardButton.setLayoutParams(layoutParams);
        }
    }

    /**
     * 提供是否显示提交按钮
     *
     * @param forwardResId 文字
     * @param show         true则显示
     */
    protected void showForwardView(int forwardResId, boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setText(forwardResId);
            } else {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 提供是否显示提交按钮
     *
     * @param title 文字
     * @param show         true则显示
     */
    protected void showForwardView(CharSequence title, boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setText(title);
                mForwardButton.setVisibility(View.VISIBLE);

            } else {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }

    /**
     * 返回按钮点击后触发
     *
     * @param backwardView
     */
    public void onBackward(View backwardView) {
//        Toast.makeText(this, "点击返回，可在此处调用finish()", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * 提交按钮点击后触发
     *
     * @param forwardView
     */
    public void onForward(View forwardView) {
        Toast.makeText(this, "点击提交", Toast.LENGTH_LONG).show();
    }

    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    //设置标题文字颜色
    @Override
    public void setTitleColor(int textColor) {
        mTitleTextView.setTextColor(textColor);
    }


    //取出FrameLayout并调用父类removeAllViews()方法
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }

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
    protected void onPause() {
        super.onPause();
        if (null != toast) {
            toast.cancel();
        }

    }

    /* (non-Javadoc)
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             * 按钮点击调用的方法
             */
    @Override
    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.button_backward:
//                onBackward(v);
//                break;
//
//            case R.id.button_forward:
//                onForward(v);
//                break;
//
//            default:
//                break;
//        }
    }

    /**
     * activity跳转
     *
     * @param c
     */
    protected void startActivity(Class c) {
        startActivity(new Intent(context, c));
    }
//
//    /**
//     * 异步网络请求类
//     *
//     * @param requestParams
//     */
//    protected void ajax(RequestParams requestParams) {
//        if (!CheckNetwork.isNetworkAvailable(context)) {
//            showToast("网络不可用，请检查网络连接！");
//            return;
//        }
//        if (loadingDialog == null) {
//            loadingDialog = new LoadingDialog(context);
//        }
//        if (requestParams != null) {
//            String uri = requestParams.getUri();
//            if (!uri.isEmpty()) {
//                String[] split = uri.split("/");
////                Logger.d("split=" + Arrays.toString(split));
//
//                String method = null;
//                try {
//                    method = split[split.length - 1].substring(0, split[split.length - 1].indexOf("."));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Logger.d("请检查服务器地址后面是否含有.html");
//                }
//                String mode = split[split.length - 2];
//                String sign = MD5.md5(mode + method);
//                requestParams.addBodyParameter("sign", sign);
//            }
//
//        }
//        Logger.d("url=" + requestParams.getUri() + "\nrequestParams=" + requestParams.getStringParams().toString());
//        List<KeyValue> params = requestParams.getStringParams();
//        for (KeyValue keyValue : params) {
//            if (keyValue.key.contains(":")) {
//                throw new RuntimeException("参数异常！");
//            }
//        }
//        //                    jsonBean = JSON.parseObject(result, JsonBean.class);
//        cancelable = x.http().post(requestParams, new Callback.ProgressCallback<String>() {
//
//            @Override
//            public void onWaiting() {
//            }
//
//            @Override
//
//            public void onStarted() {
//                netOnStart();
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isDownloading) {
//                netOnLoading(total, current, isDownloading);
//            }
//
//            @Override
//            public void onSuccess(String result) {
//
//                Logger.json(result);
//                JsonBean jsonBean = null;
//                try {
////                    jsonBean = JSON.parseObject(result, JsonBean.class);
//                    jsonBean = jsonParse(result);
//                    if (jsonBean.getMsg() != null && !jsonBean.getMsg().isEmpty()) {
//                        showToast(jsonBean.getMsg());
//                    }
//                    if (jsonBean.getCode() == 200) {
//                        Map<String, Object> data = new ArrayMap<String, Object>();
//                        if (null != jsonBean.getData() && jsonBean.getData().size() > 0) {
//                            data = jsonBean.getData();
//                        }
//                        netOnSuccess(data);
//                    } else {
//
//                        netOnOtherStates(jsonBean.getCode(), jsonBean.getMsg());
//                    }
//                } catch (Exception e) {
//                    if (loadingDialog != null && loadingDialog.isShowing()) {
//                        loadingDialog.dismiss();
//                    }
//                    Logger.d(result);
//                    showToast("服务器异常！");
//                    e.printStackTrace();
//                } finally {
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                netOnFailure(ex);
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                Logger.d("用户取消了访问网络....");
//                netOnCancelled();
//            }
//
//            @Override
//            public void onFinished() {
//                netOnFinish();
//            }
//
//        });
//    }
//
//
//    private JsonBean jsonParse(String json) throws JSONException {
//        ArrayMap<String,Object> arrayMap= JSON.parseObject(json,new TypeReference<ArrayMap<String,Object>>(){
//        }.getType());
//        JsonBean jsonBean = new JsonBean();
//        if (arrayMap.containsKey("data")) {
//            Object data = arrayMap.get("data");
//            System.out.println("data.getClass().getName()=" + data.getClass().getName());
//            ArrayMap<String,Object> rrData = null;
//            if (data instanceof String) {
//                System.out.println("data instanceof String");
//                rrData = new ArrayMap<String,Object>();
//                rrData.put("data", data.toString());
//            } else if (data instanceof JSONArray) {
//                System.out.println("data instanceof JSONArray");
//                rrData = new ArrayMap<String,Object>();
//                rrData.put("data", data);
//            } else if (data instanceof com.alibaba.fastjson.JSONObject) {
//                System.out.println("data instanceof JSONObject");
//                rrData = JSON.parseObject(data.toString(),new TypeReference<ArrayMap<String,Object>>(){
//                }.getType());
//            }
//            jsonBean.setData(rrData);
//        }
//        jsonBean.setCode(Integer.valueOf(arrayMap.get("code").toString()));
//        jsonBean.setMsg(Tools.getValue(arrayMap, "msg"));
//
//        return jsonBean;
//    }
//
//
//    /**
//     * 开始访问网络
//     */
//    public void netOnStart() {
//        loadingDialog.show("正在获取数据...");
//    }
//
//    /**
//     * 访问网络的进程
//     */
//    public void netOnLoading(long total, long current, boolean isUploading) {
//    }
//
//    /**
//     * 访问网络成功
//     */
//    public void netOnSuccess(Map<String, Object> data) {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
//    }
//
//    /**
//     * 访问网络成功的其他状态
//     */
//    public void netOnOtherStates(int code, String msg) {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
//    }
//
//    /**
//     * 访问网络结束
//     */
//    public void netOnFinish() {
//        loadingDialog.dismiss();
//    }
//
//    /**
//     * 访问网络失败
//     */
//    public void netOnFailure(Throwable ex) {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
//        Logger.d(ex.getMessage());
//        if (ex instanceof HttpException) { // 网络错误
//            HttpException httpEx = (HttpException) ex;
//            int responseCode = httpEx.getCode();
//            String responseMsg = httpEx.getMessage();
//            String errorResult = httpEx.getResult();
//            Toast.makeText(x.app(), "网络错误：" + ex.getMessage(), Toast.LENGTH_LONG).show();
//            // ...
//        } else { // 其他错误
//            Toast.makeText(context, "连接服务器失败，请稍后再试！ex=" + ex.getMessage(), Toast.LENGTH_SHORT).show();
//            // ...
//        }
//
//
//    }
//
//    /**
//     * 取消访问网络
//     */
//    public void netOnCancelled() {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
//    }

    @Override
    public void onBackPressed() {
//        Logger.d("onBackPressed");
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();

            if (cancelable != null && !cancelable.isCancelled()) {
                cancelable.cancel();
            }
        } else {
            super.onBackPressed();
        }

    }

    protected void setEmptyView(ListView listView) {
        TextView emptyView = new TextView(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContentLayout.removeAllViews();
        mContentLayout=null;
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}

