package com.renygit.recycleviewdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.renygit.recycleview.RRecyclerView;
import com.renygit.recycleview.RStyleConfig;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

public class MainActivity extends AppCompatActivity {

    RRecyclerView rv;
    List<String> items;
    MyAdapter adapter;

    int pageCount = 10;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RRecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //可以设置的参数见 RStyleConfig
        rv.setConfig(new RStyleConfig.Build().setTipLoading("努力加载中").setTipError("哎呀，发生错误了").setTipEnd("我是底线").build());

        rv.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.setLoading();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addItems(20);
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
        rv.setOnLoadMoreListener(new RRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //rv.setLoading(); 滚动时会自动触发
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addItems(20);
                        adapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        addItems(20);
        adapter = new MyAdapter(rv);
        adapter.setData(items);
        rv.setAdapter(adapter);

    }

    public boolean isConnected() {
        NetworkInfo net = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void addItems(int size){
        if(page >= pageCount){
            rv.setNoMore(true);
            return;
        }
        if(!isConnected()){
            rv.setError();
            return;
        }
        if(null == items){
            items = new ArrayList<>();
        }
        for (int i = 0; i < size; i++) {
            items.add("测试一个上拉加载更多的Demo "+i);
        }
        page++;
        rv.loadComplete();
    }

    private class MyAdapter extends BGARecyclerViewAdapter<String>{

        public MyAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            helper.setText(R.id.tv, model);
        }
    }


}
