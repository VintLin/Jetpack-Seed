package com.example.base.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.multidex.MultiDexApplication;

import com.example.base.util.ManifestUtil;
import com.example.base.util.once.Once;
import com.example.base.util.ProcessHelper;

import java.util.LinkedList;
import java.util.List;

import me.jessyan.autosize.AutoSize;

public class App extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, ViewModelStoreOwner {
    private ViewModelStore mAppViewModelStore;

    public static int mMainThreadTid;//主线程ID
    public static Looper mMainLooper;
    public static Handler mHandler;

    @SuppressLint("StaticFieldLeak")
    private static App instance;
    private static Bundle metaData;

    public Activity curActivity;

    public Bundle METADATA;
    public String packName;
    public String versionName;
    public int versionCode;

    private final List<Activity> activityList = new LinkedList<>();

    public static App get() {
        return instance;
    }

    public static App get(Context context) {
        try {
            if (instance == null && context != null) {
                instance = (App) context.getApplicationContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static Bundle getMetaData() {
        if (metaData == null) {
            metaData = ManifestUtil.getAppMetaData();//获取META数据
        }
        return metaData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        // 初始化 Store
        mAppViewModelStore = new ViewModelStore();
        // 初始化 Once
        Once.initialise(this);
        // 初始化 AutoSize
        AutoSize.initCompatMultiProcess(this);

        dealPackageInfo();

        dealAppInfo();
    }

    private void dealAppInfo() {
        instance = this;

        METADATA = ManifestUtil.getAppMetaData();//获取META数据
        packName = getPackageName();//处理包名

        mMainThreadTid = Process.myTid();
        mHandler = new Handler();
        mMainLooper = getMainLooper();
    }

    private void dealPackageInfo() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Android P 行为变更，不可多进程使用同一个目录webView，需要为不同进程webView设置不同目录。
     * 需要在Application onCreate时调用检查
     */
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = ProcessHelper.getCurrentProcessName(this);
            if (!getApplicationContext().getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName + "webview");
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 删除Activity到容器中
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public Activity getCurrentAct() {
        if (curActivity == null || curActivity.isDestroyed() || curActivity.isFinishing()) {
            if (curActivity != null) {
                activityList.remove(curActivity);
            }
            if (activityList.size() > 0) {
                curActivity = activityList.get(activityList.size() - 1);
            }
        }
        return curActivity;
    }

    public Activity getActivityByString(String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        if (activityList.size() > 0) {
            for (Activity activity : activityList) {
                if (TextUtils.equals(className, activity.toString())) {
                    return activity;
                }
            }
        }
        return null;
    }

    public Activity getLastAct() {
        if (activityList.size() >= 2) {
            return activityList.get(activityList.size() - 2);
        }
        return null;
    }

    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        if (!activityList.isEmpty()) {
            for (int i = activityList.size() - 1; i >= 0; i--) {
                Activity activity = activityList.get(i);
                if (activity == null) {
                    continue;
                }
                if (activity.isFinishing()) {
                    continue;
                }
                activity.finish();
            }
        }
    }

    public void exitImmediately() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    public void restart() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        curActivity = activity;
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        instance = this;
        curActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        curActivity = null;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        curActivity = null;
        removeActivity(activity);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }
}
