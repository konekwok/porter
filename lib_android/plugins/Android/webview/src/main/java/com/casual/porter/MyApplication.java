package com.casual.porter;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

//import androidx.multidex.MultiDex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication _currentApplication ;

    private volatile Activity m_objCurrentActivity=null;

    public static MyApplication getInstance() {
        return _currentApplication;
    }

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = this;
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        _currentApplication = this;
    }

    private static String processName;

    public static String getProcess() {
        if (TextUtils.isEmpty(processName)) {
            processName = searchProcessName();
        }

        return processName;
    }

    private static String searchProcessName() {
        String processName = null;

        try {
            File file = new File("/proc/" + Process.myPid() + "/cmdline");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            processName = bufferedReader.readLine().trim();
            bufferedReader.close();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        if (TextUtils.isEmpty(processName)) {
            ActivityManager activityManager = (ActivityManager) sContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            if (processInfos != null) {
                int pid = Process.myPid();
                Iterator var4 = processInfos.iterator();

                while (var4.hasNext()) {
                    ActivityManager.RunningAppProcessInfo appProcess = (ActivityManager.RunningAppProcessInfo) var4.next();
                    if (appProcess.pid == pid) {
                        processName = appProcess.processName;
                        break;
                    }
                }
            }
        }

        return processName;
    }

    public synchronized Activity getCurrentActivity() {

        return m_objCurrentActivity;
    }

    public synchronized void setCurrentActivity(Activity activity) {

        m_objCurrentActivity = activity;
    }
}
