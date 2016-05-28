package com.jiusg.course.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.jiusg.course.R;
import com.jiusg.course.base.BaseActivity;
import com.jiusg.course.base.BaseApplication;
import com.jiusg.course.domain.CurrentTime;
import com.jiusg.course.domain.Schedule;
import com.jiusg.course.domain.Term;
import com.jiusg.course.ui.Adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 主要展示课表
 */

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private NavigationView navigationView;

    private TextView username;
    private TextView currentTime;

    private SwipeRefreshLayout swipeRefreshLayout;
    private GridView gridView;

    private MainHandler handler;

    private MainAdapter adapter;

    private SharedPreferences preferences;

    private String[][] courseInfo = new String[6][6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        currentTime = (TextView) navigationView.getHeaderView(0).findViewById(R.id.currentTime);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        gridView = (GridView) findViewById(R.id.gridView);

        swipeRefreshLayout.setOnRefreshListener(this);

        initCourseInfo();

        handler = new MainHandler();
        adapter = new MainAdapter(this, courseInfo);
        gridView.setAdapter(adapter);

        preferences = getSharedPreferences("user", MODE_PRIVATE);

        handler.sendEmptyMessage(1);

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 100);
            }
        });

    }

    /**
     * 用户登陆
     */
    public void login() {

        final String username = preferences.getString("username", "");
        final String password = preferences.getString("password", "");

        if ("".equals(username)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, 100);
            return;
        }

        AVUser.logInInBackground(username, password,
                new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (null == e) {
                            BaseApplication.user = avUser;
                            updateUserInfoUI();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, 100);
                        }
                    }
                });

    }


    private void updateUserInfoUI() {

        if (null == BaseApplication.user)
            return;

        username.setText(BaseApplication.user.getString("name") + "");

        // 请求服务器当前时间

        AVQuery<AVObject> query = new AVQuery<>("CurrentTime");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (null == e && list.size() > 0) {
                    list.get(0).getAVObject("term")
                            .fetchInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject avObject, AVException e) {
                                    if (null == e) {
                                        CurrentTime currentTime = new CurrentTime();
                                        currentTime.week = list.get(0).getInt("week") + "";
                                        Term term = new Term();
                                        term.termNum = avObject.getInt("termNum");
                                        term.year = avObject.getString("year");
                                        term.objectId = avObject.getObjectId();
                                        currentTime.term = term;
                                        BaseApplication.currentTime = currentTime;
                                        MainActivity.this.currentTime
                                                .setText(term.year + "学年 第" + (term.termNum + 1) + "学期" + " 第" + currentTime.week + "周");
                                        setTitle("第" + currentTime.week + "周");
                                        Log.i("test", "当前时间信息加载完成!");

                                        // 请求该学生课表
                                        loadCourseInfo();

                                    }
                                }
                            });

                }
            }
        });

    }


    /**
     * 加载课表信息
     */
    private void loadCourseInfo() {

        if (null == BaseApplication.user)
            return;


        AVQuery<AVObject> query = new AVQuery<>("Schedule");
        query.whereEqualTo("term",
                AVObject.createWithoutData("Term",
                        BaseApplication.currentTime.term.objectId));
        query.whereEqualTo("user",
                AVObject.createWithoutData("_User",
                        BaseApplication.user.getObjectId()));
        query.whereContains("classWeek", "[" + BaseApplication.currentTime.week + "]");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (null == e) {
                    ArrayList<Schedule> schedules = new ArrayList<Schedule>();
                    for (AVObject object : list) {
                        Schedule schedule = new Schedule();
                        schedule.setWeek(Integer.parseInt(BaseApplication.currentTime.week));
                        schedule.setClassInfo(object.getString("classInfo"));
                        schedule.setClassNam(object.getString("classNam"));
                        schedule.setClassPlace(object.getString("classPlace"));
                        schedule.setClassTeacher(object.getString("classTeacher"));
                        schedule.setClassDate(object.getInt("classDate"));
                        schedule.setClassTime(object.getInt("classTime"));
                        schedules.add(schedule);
                    }
                    converCourseInfo(schedules);
                    Log.i("test", "当前课表信息请求成功 size=" + list.size());
                }
            }
        });
    }

    private void converCourseInfo(ArrayList<Schedule> schedules) {

        initCourseInfo();

        for (Schedule schedule : schedules) {
            String str = schedule.getClassNam() + "\n"
                    + schedule.getClassTeacher() + "\n"
                    + schedule.getClassPlace() + "\n"
                    + schedule.getClassInfo() + "\n";
            if (schedule.getClassTime() < 5 && schedule.getClassDate() < 5
                    && schedule.getClassTime() > -1 && schedule.getClassDate() > -1) {
                courseInfo[schedule.getClassTime() + 1][schedule.getClassDate() + 1] = str;
            }
        }

        handler.sendEmptyMessage(0);

    }

    private void initCourseInfo() {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                courseInfo[i][j] = "";
            }
        }

        courseInfo[0][1] = "星期一";
        courseInfo[0][2] = "星期二";
        courseInfo[0][3] = "星期三";
        courseInfo[0][4] = "星期四";
        courseInfo[0][5] = "星期五";

        courseInfo[1][0] = "一二节\n8:00-9:45";
        courseInfo[2][0] = "三四节\n10:05-11：50";
        courseInfo[3][0] = "五六节\n14:00-15:45";
        courseInfo[4][0] = "七八节\n16:05-17:50";
        courseInfo[5][0] = "九十节\n19:00-18:45";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUserInfoUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void testLeanCloud() {
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words", "Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("saved", "success!");
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_course) {
            // Handle the camera action
        } else if (id == R.id.nav_homework) {

            startActivity(new Intent(MainActivity.this, HomeworkActivity.class));

        } else if (id == R.id.nav_message) {

            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_score) {
            startActivity(new Intent(MainActivity.this, ScoreActivity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        updateUserInfoUI();
    }


    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    login();
                    break;
            }
        }
    }
}
