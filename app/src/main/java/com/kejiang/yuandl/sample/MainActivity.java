package com.kejiang.yuandl.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kejiang.yuandl.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public void initTitleBar() {
        setTitle("试试");

    }

    @Override
    public void initViews() {

        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {

    }

    @Override
    public void addListener() {

    }
}
