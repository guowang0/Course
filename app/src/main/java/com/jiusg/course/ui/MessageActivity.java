package com.jiusg.course.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.jiusg.course.R;
import com.jiusg.course.base.BaseActivity;
import com.jiusg.course.base.BaseApplication;
import com.jiusg.course.domain.Schedule;
import com.jiusg.course.ui.Adapter.HomeWorkAdapter;
import com.jiusg.course.ui.Adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 * 留言界面
 */
public class MessageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ImageButton add;
    private ImageButton back;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    private MessageHandler handler;

    private ArrayList<com.jiusg.course.domain.Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        handler = new MessageHandler();

        adapter = new MessageAdapter(messages,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void initView(){
        add = (ImageButton) findViewById(R.id.add);
        back = (ImageButton) findViewById(R.id.back);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MessageAddActivity.class));
            }
        });
    }

    private void loadData(){
        if (null == BaseApplication.user) {
            showToast("你还没有登陆!");
            return;
        }

        if (null == BaseApplication.currentTime) {
            showToast("未知时间!");
            return;
        }

        messages.clear();

        AVQuery<AVObject> query = new AVQuery<>("Message");
        query.whereEqualTo("term",
                AVObject.createWithoutData("Term",
                        BaseApplication.currentTime.term.objectId));
        query.whereEqualTo("user",
                AVObject.createWithoutData("_User",
                        BaseApplication.user.getObjectId()));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject object:list
                     ) {
                    com.jiusg.course.domain.Message message = new com.jiusg.course.domain.Message();
                    message.content = object.getString("content");
                    message.className = object.getString("classNam");
                    message.userName = object.getString("username");
                    messages.add(message);
                }

                handler.sendEmptyMessage(1);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(0);
    }

    class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    swipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                    break;
                case 1:
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

}
