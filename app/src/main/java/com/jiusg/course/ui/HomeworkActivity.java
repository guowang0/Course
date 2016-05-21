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
import android.widget.TextView;

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
import com.jiusg.course.ui.Adapter.HomeWorkAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/20.
 * 作业展示界面
 */
public class HomeworkActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageButton back;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HomeWorkAdapter adapter;

    private ArrayList<Homework> homeworks = new ArrayList<>();

    private HomeWorkHandler handler;

    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView(toolbar);

        handler = new HomeWorkHandler();

        handler.sendEmptyMessageDelayed(0,200);

        adapter = new HomeWorkAdapter(homeworks,this);

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

        homeworks.clear();

        AVQuery<AVObject> query = new AVQuery<>("HomeWork");
        query.whereEqualTo("term",
                AVObject.createWithoutData("Term",
                        BaseApplication.currentTime.term.objectId));
        query.whereEqualTo("user",
                AVObject.createWithoutData("_User",
                        BaseApplication.user.getObjectId()));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (null == e) {

                    final int size = list.size();
                    count = 0;

                    Log.i("test","homeWorks="+list.size());

                    for (final AVObject object : list) {

                        object.getAVObject("course")
                                .fetchInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        if(null == e){
                                            Homework homeWork = new Homework();
                                            homeWork.content = object.getString("content");
                                            homeWork.isFinish = object.getBoolean("isFinish");
                                            homeWork.time = object.getString("time");
                                            homeWork.objectId = object.getObjectId();
                                            Schedule schedule = new Schedule();
                                            schedule.setWeek(Integer.parseInt(BaseApplication.currentTime.week));
                                            schedule.setClassInfo(avObject.getString("classInfo"));
                                            schedule.setClassNam(avObject.getString("classNam"));
                                            schedule.setClassPlace(avObject.getString("classPlace"));
                                            schedule.setClassTeacher(avObject.getString("classTeacher"));
                                            schedule.setClassDate(avObject.getInt("classDate"));
                                            schedule.setClassTime(avObject.getInt("classTime"));
                                            homeWork.schedule = schedule;

                                            homeworks.add(homeWork);

                                            count++;

                                            if(count >= size){
                                                handler.sendEmptyMessage(1);
                                            }


                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        loadData();
    }


    class HomeWorkHandler extends Handler {

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
