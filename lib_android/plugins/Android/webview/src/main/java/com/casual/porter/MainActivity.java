package com.casual.porter;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.unity3d.player.UnityPlayerActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends UnityPlayerActivity {

    private Context mContext;

    private NetWorkStateReceiver netWorkStateReceiver;

    ExecutorService mExecutors = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdBlocker.init(this);
        Window _window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtils.HideStatusBar(_window);
        }
        _window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        StatusBarUtils.HideStatusBar(getWindow());
                    }
                }
            }
        });
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        mContext = this;

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //注册网络状态监听器
        if (netWorkStateReceiver == null) {

            netWorkStateReceiver = new NetWorkStateReceiver(this);
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(netWorkStateReceiver, filter);
        ////////////////////////////////////////////////////////////////////////////////////////////////

        Log.d("Unity", "MainActivity onCreate: ");
    }
//    public void ConfigUnityCallback(IUnityCallback callback)
//    {
//        this.unityCallback = callback;
//        mExecutors.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                String strOut = AdvertisingIdClient.getGoogleAdId( Match3Application.getContext() );
//
//                if( unityCallback != null)
//                {
//                    unityCallback.OnGoogleAdId(strOut);
//                }
//            }
//        });
//        String strNetStatus = "none" ;
//        try
//        {
//            ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (connectivity != null) {
//
//                NetworkInfo info = connectivity.getActiveNetworkInfo();
//                if (info != null && info.isConnected()) {
//
//                    if (info.getState() == NetworkInfo.State.CONNECTED) {
//                        strNetStatus = "connected" ;
//                    }
//                }
//            }
//        }
//        catch (Exception e) {}
//        OnConnectChanged(strNetStatus);
//    }
    @Override
    protected void onPause()
    {
        super.onPause();

        //注销网络监听
        //unregisterReceiver(netWorkStateReceiver);

        //Force unity to release the wakelock
        //setWakeLock(false);
    }
    public void OnConnectChanged(String strNetStatus)
    {
//        final String finalStrNetStatus = strNetStatus;
//        mExecutors.execute(new Runnable() {
//            @Override
//            public void run() {
//                if(unityCallback != null)
//                {
//                    unityCallback.OnConnectivityChanged(finalStrNetStatus);
//                }
//            }
//        });
    }
    /**
     * 拉取一个email的chooser，发送邮件
     */
    public String sendEmail(String version)
    {
        try
        {
            String content = "My problem/suggestion is:\n\n\n\n\n\n————————————————————\n The team of Cookie Crunch really appreciates your feedback."+
                    "All the information below is important for us to track and solve the problem you encountered.Please do not delete them!\n"+
                    "App Version = " + version;
            Uri uri = Uri.parse("mailto:match3feedbackinbox@outlook.com");
            Intent intent= new Intent (Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Cookie Crunch Feedback");
            intent.putExtra(Intent.EXTRA_TEXT,content);

            Intent chooserIntent = Intent.createChooser(intent,"Send your idea！");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(chooserIntent);
            return "Succeed";
        }
        catch (Exception e)
        {
            return "Failed :" +e.toString();
        }

    }
    /**
     * 调用Unity的方法
     * @param gameObjectName    调用的GameObject的名称
     * @param functionName      方法名
     * @param args              参数
     * @return                  调用是否成功
     */
    boolean callUnity(String gameObjectName, String functionName, String args) {

        try {

            Class<?> classType = Class.forName("com.unity3d.player.UnityPlayer");

            Method method = classType.getMethod("UnitySendMessage", String.class,String.class,String.class);

            method.invoke(classType,gameObjectName,functionName,args);

            return true;
        }
        catch (ClassNotFoundException e) {

        }
        catch (NoSuchMethodException e) {

        }
        catch (IllegalAccessException e) {

        }
        catch (InvocationTargetException e) {

        }
        return false;
    }

    /**
     * Toast显示unity发送过来的内容
     * @param content           消息的内容
     * @return                  调用是否成功
     */
    public boolean showToast(String content){
        Toast.makeText(mContext,content,Toast.LENGTH_SHORT).show();
        //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
        callUnity("Main Camera","FromAndroid", "hello unity i'm android");
        return true;
    }


    /**
     * 获取手机品牌
     */
    public String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     *  Category 判断是否平板设备
     * @return true:平板,false:手机
     */

    public  void CallOtherActivity()
    {
        //OtherActivity.start(this.mContext);
    }
    private boolean isTabletDevice() {
        return (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取手机型号
     */
    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统类型
     */
    public String getOperatingSystem(){
        return "Android";
    }

    /**
     * 获取手机Android 系统SDK
     */
    public int getDeviceSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本
     */
    public String getDeviceAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * ID
     */
    public String getDeviceId() {
//        return Settings.System.ANDROID_ID;
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
//        return android.os.Build.ID;
    }

    /**
     * 获取当前手机系统语言。
     */
    public String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 生成UUID
     */
    public String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 获取当前系统设置的国家编号，比如中国大陆 CN
     */
    public String getCurrentCountry() {
        if (mContext == null) {
            return "";
        }
        String countryCode = getSimCountryCode();
        return (TextUtils.isEmpty(countryCode) ? getLocalCountry() : countryCode).toUpperCase();
    }

    public String getSimCountryCode() {
        String result = "";
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (!TextUtils.isEmpty(telephonyManager.getSimCountryIso())) {
                result = telephonyManager.getSimCountryIso().trim();
            } else if (!TextUtils.isEmpty(telephonyManager.getNetworkCountryIso())) {
                result = telephonyManager.getNetworkCountryIso().trim();
            }
        }
        return result;
    }
    public boolean isNetworkConnected() {
        if (mContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public String getLocalCountry() {
        return Locale.getDefault().getCountry().toUpperCase();
    }

//    public void getGoogleAdId(IUnityGoogleAdIdCallback callback)
//    {
//        driverlessGAIDCallback = callback ;
//
//        mExecutors.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                String strOut = AdvertisingIdClient.getGoogleAdId( Match3Application.getContext() );
//
//                if( driverlessGAIDCallback != null)
//                {
//                    driverlessGAIDCallback.OnGoogleAdId(strOut);
//                }
//            }
//        });
//
//    }

    public String getVersionCode() {

        String versionCode="";

        if (mContext == null)  return versionCode;

        PackageManager packageManager= mContext.getPackageManager();

        PackageInfo packageInfo;

        try {

            packageInfo=packageManager.getPackageInfo(mContext.getPackageName(),0);

            versionCode=packageInfo.versionCode+"";
        }
        catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return versionCode;
    }

    public void doRestart(int Ntime)
    {
        Intent restartIntent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(this, 0,restartIntent,0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis()+Ntime, intent);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void startFacebook(String facebookUrl) {
        try {
            Uri uri = null;
            int versionCode = getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0)
                    .versionCode;
            if (versionCode >= 3002850) {
                facebookUrl = facebookUrl.toLowerCase().replace("www.", "m.");
                if (!facebookUrl.startsWith("https")) {
                    facebookUrl = "https://" + facebookUrl;
                }
                uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
            } else {
                String pageID = facebookUrl.substring(facebookUrl.lastIndexOf("/"));
                uri = Uri.parse("fb://page" + pageID);
            }
//            Log.d("startFacebook", "startFacebook: uri = " + uri.toString());
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Throwable e) {
            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
            catch(Throwable ee)
            {

            }
        }
    }

    public String getScreenWidthDP() {
        WindowManager windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        int width = (int)(displayMetrics.widthPixels * 160.0 / displayMetrics.densityDpi);
        return Integer.toString(width);
    }

    public String getScreenHeightDP() {
        WindowManager windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        int height = (int)(displayMetrics.heightPixels * 160.0 / displayMetrics.densityDpi);
        return  String.valueOf(height);
    }
    public int getTotalMemory() {
//        String str1 = "/proc/meminfo";// 系统内存信息文件
//        String str2;
//        String[] arrayOfString;
        int initial_memory = 0;
//
//        try {
//            FileReader localFileReader = new FileReader(str1);
//            BufferedReader localBufferedReader = new BufferedReader(
//                    localFileReader, 8192);
//            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
//
//            arrayOfString = str2.split("\\s+");
//            for (String num : arrayOfString) {
//                Log.i(str2, num + "\t");
//            }
//
//            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;// 获得系统总内存，单位是KB，除以1024转换为MB
//            localBufferedReader.close();
//
//        } catch (IOException e) {
//        }
        ActivityManager activityManager = (ActivityManager)(this.mContext.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        initial_memory = (int) (memoryInfo.totalMem / (1024 * 1024));
        return initial_memory;
    }
    public  int getAvailMemory()
    {
        ActivityManager activityManager = (ActivityManager)(this.mContext.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        int initial_memory = (int) (memoryInfo.availMem / (1024 * 1024));
        return initial_memory;
    }
    public int getAppTotalMemory()
    {
//        ActivityManager activityManager = (ActivityManager)(this.mContext.getSystemService(Context.ACTIVITY_SERVICE));
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(memoryInfo);
//        int initial_memory = activityManager.getMemoryClass();
//        return initial_memory;
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() * 1.0/ (1024 * 1024));

        //当前分配的总内存
        return totalMemory;
    }
    public int getAppMaxMemory()
    {
        int totalMemory = (int) (Runtime.getRuntime().maxMemory() * 1.0/ (1024 * 1024));
        //当前分配的总内存
        return totalMemory;
    }
    public String getCpuModel() {

        String str1 = "/proc/cpuinfo";
        String str2 = "";

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (str2.contains("Hardware")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return "";
    }

    public String getFingerPrint() {
        return Build.FINGERPRINT;
    }

    private String userAgent = "";
    public String getUserAgent() {
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = System.getProperty("http.agent", "");
        }
        if (!TextUtils.isEmpty(userAgent)) {
            return userAgent;
        }
        else {
            // WebViews may only be instantiated on the UI thread. If anything goes
            // wrong with getting a user agent, use the system-specific user agent.
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // Since we are not on the main thread, return the default user agent
                // for now. Defer to when this is run on the main thread to actually
                // set the user agent.
                return userAgent;
            }

            // Some custom ROMs may fail to get a user agent. If that happens, return
            // the Android system user agent.
            String tempUserAgent = "";
            try {
                tempUserAgent = WebSettings.getDefaultUserAgent(mContext);
            } catch (Exception e) {
            }

            if (!TextUtils.isEmpty(tempUserAgent)) {
                userAgent = tempUserAgent;
            }
            return userAgent;
        }
    }

    public String getNetworkType() {
        int type = CheckNetWorkType();
        switch (type) {
            case 0:
                return "nonetwork";
            case 1:
                return "wifi";
            case 2:
                return "2g";
            case 3:
                return "3g";
            case 4:
                return "4g";
            default:
                return "other";
        }
    }

    private int CheckNetWorkType() {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(
                    Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (telephonyManager == null || telephonyManager.isNetworkRoaming()) {
                netType = 2;
                return netType;
            }

            switch (nSubType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_GSM:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                default:
                    netType = 2;
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    netType = 3;
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_IWLAN:
                    netType = 4;
                    break;
            }
        }
        return netType;
    }
}
