package com.jiusg.course.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.jiusg.course.R;
import com.jiusg.course.base.BaseActivity;
import com.jiusg.course.base.BaseApplication;

import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 */
public class MessageAddActivity extends BaseActivity{

    private ImageButton back;
    private Button enter;
    private EditText classNam;
    private EditText content;

    private String[] strings = null;
    private String[] ids = null;
    private int choose = 0;

    private ProgressDialog pd = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageadd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        classNam = (EditText) findViewById(R.id.username);
        content = (EditText) findViewById(R.id.password);
        enter = (Button) findViewById(R.id.login);
        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        classNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = ProgressDialog.show(MessageAddActivity.this, null,
                        "加载中..", true);

                AVQuery<AVObject> query = new AVQuery<>("Schedule");
                query.whereEqualTo("term",
                        AVObject.createWithoutData("Term",
                                BaseApplication.currentTime.term.objectId));
                query.whereEqualTo("user",
                        AVObject.createWithoutData("_User",
                                BaseApplication.user.getObjectId()));
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {

                        if(null == e && list.size() > 0){
                            strings = new String[list.size()];
                            ids = new String[list.size()];
                            for (int i = 0; i< list.size();i++){
                                strings[i] = list.get(i).getString("classNam");
                                ids[i] = list.get(i).getObjectId();
                            }

                        }

                        pd.dismiss();

                        if(null == strings || strings.length == 0){
                            showToast("没有数据");
                            return;
                        }

                        new AlertDialog.Builder(MessageAddActivity.this)
                                .setTitle("请选择")
                                .setSingleChoiceItems(strings, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        choose = which;
                                        classNam.setText(strings[which]);
                                        dialog.dismiss();
                                    }
                                }).show();

                    }
                });

            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("".equals(classNam.getText().toString()) || "".equals(content.getText().toString())){
                    showToast("不能为null");
                    return;
                }

                if(null == BaseApplication.currentTime || null == BaseApplication.user){
                    showToast("获取数据失败");
                    return;
                }

                pd = ProgressDialog.show(MessageAddActivity.this, null,
                        "加载中..", true);

                AVObject object = new AVObject("Message");
                object.put("term",AVObject.createWithoutData("Term",
                        BaseApplication.currentTime.term.objectId));
                object.put("user",AVObject.createWithoutData("_User",
                        BaseApplication.user.getObjectId()));
                object.put("username",BaseApplication.user.getString("name"));
                object.put("content",content.getText().toString());
                object.put("classNam",classNam.getText().toString());
                object.put("schedule",AVObject.createWithoutData("Schedule",ids[choose]));

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {

                        pd.dismiss();

                        if(e!= null){
                            showToast("提交失败");
                            return;
                        }

                        finish();
                    }
                });

            }
        });

    }
}
