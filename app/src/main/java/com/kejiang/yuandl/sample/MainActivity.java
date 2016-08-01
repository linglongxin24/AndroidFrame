package com.kejiang.yuandl.sample;

import android.widget.ListView;

import com.kejiang.yuandl.adapter.common.ViewHolder;
import com.kejiang.yuandl.adapter.common.abslistview.CommonAdapter;
import com.kejiang.yuandl.base.BaseActivity;
import com.kejiang.yuandl.view.RatingBar;
import com.ldd.pullview.AbPullToRefreshView;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private RatingBar rb;
    private AbPullToRefreshView pr;
    private ListView listView;
    public static final String ADDRESS_LIST = "http://220.191.205.232:9003/WZ_WebService.asmx/GetEmpInfo";// 客户
    private void assignViews() {
        rb = (RatingBar) findViewById(R.id.rb);
        pr = (AbPullToRefreshView) findViewById(R.id.pr);
        listView = (ListView) findViewById(R.id.listView);
    }


    @Override
    public void initTitleBar() {
        setTitle("试试");

    }

    @Override
    public void initViews() {
        setContentView(R.layout.ac_main);
        assignViews();
    }

    @Override
    public void initData() {
        rb.setStar(4.5f);
        List<String> datas=new ArrayList<>();
        listView.setAdapter(new CommonAdapter<String>(context,R.layout.item_text,datas){
            @Override
            public void convert(ViewHolder holder, String s) {

            }
        });
        pr.setOnHeaderRefreshListener(new AbPullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(AbPullToRefreshView paramAbPullToRefreshView) {

            }
        });
        pr.setOnFooterLoadListener(new AbPullToRefreshView.OnFooterLoadListener() {
            @Override
            public void onFooterLoad(AbPullToRefreshView paramAbPullToRefreshView) {

            }
        });
//        ajax(new RequestParams(ADDRESS_LIST));
    }

    @Override
    public void addListener() {

    }
}
