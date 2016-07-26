package com.kejiang.yuandl.base;

import android.view.View;
import android.view.ViewStub;

import com.kejiang.yuandl.R;

import java.util.Map;

/**
 * com.bm.falvzixun.activities.BaseActivity;
 *
 * @author yuandl on 2015/12/17.
 *         所有页面的基类
 */
public abstract class BaseNetErrorActivity extends BaseActivity {
    private boolean isInflate = false;

    @Override
    protected void netOnSuccess(Map<String, Object> data, int requestCode) {
        super.netOnSuccess(data, requestCode);
        (findViewById(R.id.vs)).setVisibility(View.GONE);
        getContentLayout().setVisibility(View.VISIBLE);
    }

    @Override
    protected void netOnFailure(Throwable ex, int requestCode) {
        super.netOnFailure(ex, requestCode);
        getContentLayout().setVisibility(View.GONE);
        if (!isInflate) {
            ((ViewStub) findViewById(R.id.vs)).inflate();
            isInflate = true;
        } else {
            (findViewById(R.id.vs)).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.bt_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reLoad();
            }
        });
    }

    /**
     * 重新加载数据
     */
    protected abstract void reLoad();
}

