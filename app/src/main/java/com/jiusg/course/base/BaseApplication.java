package com.jiusg.course.base;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.jiusg.course.domain.CurrentTime;

/**
 * Created by Administrator on 2016/5/10.
 */
public class BaseApplication extends Application{

    public static AVUser user = null;
    public static CurrentTime currentTime = null;

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this,"mP00DGoO3rbu06ixgtJDisVU-gzGzoHsz","THa0Ex19YOhvLV7qy9vBADKE");

    }
}
