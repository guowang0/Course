package com.jiusg.course.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.jiusg.course.domain.Homework;
import com.jiusg.course.domain.Schedule;
import com.jiusg.course.domain.Score;
import com.jiusg.course.ui.Adapter.HomeWorkAdapter;
import com.jiusg.course.ui.Adapter.ScoreAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/20.
 * 作业展示界面
 */
public class ScoreActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageButton back;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;

    private ArrayList<Score> scores = new ArrayList<>();

    private ScoreHandler handler;

    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView(toolbar);

        handler = new ScoreHandler();

        handler.sendEmptyMessageDelayed(0,200);

        adapter = new ScoreAdapter(scores,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void initView(Toolbar  toolbar) {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (null == BaseApplication.user) {
            showToast("你还没有登陆!");
            return;
        }

        if (null == BaseApplication.currentTime) {
            showToast("未知时间!");
            return;
        }

        scores.clear();

        AVQuery<AVObject> query = new AVQuery<>("Score");
        query.whereEqualTo("user",
                AVObject.createWithoutData("_User",
                        BaseApplication.user.getObjectId()));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (null == e) {

                    for (AVObject object: list
                         ) {

                        Score score = new Score();
                        score.classNum = object.getString("classNam");
                        score.score = object.getInt("score");
                        score.objectId = object.getObjectId();
                        scores.add(score);
                    }
                    handler.sendEmptyMessage(1);
                    }
                }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }


    class ScoreHandler extends Handler {

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
