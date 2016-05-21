package com.jiusg.course.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.jiusg.course.R;
import com.jiusg.course.base.BaseActivity;
import com.jiusg.course.base.BaseApplication;

/**
 * Created by Administrator on 2016/5/10.
 * 登陆界面
 */
public class LoginActivity  extends BaseActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private ImageButton back;

    private SharedPreferences preferences;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        back = (ImageButton) findViewById(R.id.back);

        preferences = getSharedPreferences("user",MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("".equals(username.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            "请填写用户名",Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = ProgressDialog.show(LoginActivity.this, null,
                                           "登陆中..", true);

                AVUser.logInInBackground(username.getText().toString(),
                        password.getText().toString(),
                        new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                progressDialog.dismiss();
                                if(null == e) {
                                    BaseApplication.user = avUser;
                                    preferences.edit()
                                            .putString("username",username.getText()
                                                    .toString()).apply();
                                    preferences.edit()
                                            .putString("password",password.getText()
                                                    .toString()).apply();
                                    setResult(100);
                                    finish();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),
                                            e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
