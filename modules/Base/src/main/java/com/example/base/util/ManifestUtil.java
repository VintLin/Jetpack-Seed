package com.example.base.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.base.app.App;

public class ManifestUtil {
    public final static int APPLICATION = 0;
    public final static int ACTIVITY = 1;
    public final static int SERVICE = 2;
    public final static int RECEIVER = 3;

    /**
     * 获得应用MetaData
     *
     * @return
     */
    public static Bundle getAppMetaData() {
        ApplicationInfo info = null;
        try {
            PackageManager packageManager = App.get().getPackageManager();
            if(packageManager == null){
                return  null;
            }
            info = packageManager.getApplicationInfo(App.get().getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        return info.metaData;
    }

    /**
     * 获得Activity的MetaData
     *
     * @param act
     * @return
     */
    public static Bundle getActivityMetaData(Activity act) {
        ActivityInfo info = null;
        try {
            info = act.getPackageManager().getActivityInfo(act.getComponentName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        return info.metaData;
    }

    /**
     * 获得Service的MetaData
     *
     * @param cls
     * @return
     */
    public static Bundle getServiceMetaData(Class<?> cls) {
        ComponentName cn = new ComponentName(App.get(), cls);
        ServiceInfo info = null;
        try {
            info = App.get().getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        return info.metaData;
    }

    /**
     * 获得Receiver的MetaData
     *
     * @param cls class
     * @return ReceiverMetaData
     */
    public static Bundle getReceiverMetaData( Class<?> cls) {
        ComponentName cn = new ComponentName(App.get(), cls);
        ActivityInfo info = null;
        try {
            info = App.get().getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        return info.metaData;
    }

    /**
     * 获得不同类型的MetaData
     *
     * @param context context
     * @param type type
     * @param cls class
     * @return MetaData
     */
    public static Bundle getMetaData(Activity context, int type, Class<?> cls) {
        switch (type) {
            case APPLICATION:
                return getAppMetaData();
            case ACTIVITY:
                return getActivityMetaData(context);
            case SERVICE:
                return getServiceMetaData(cls);
            case RECEIVER:
                return getReceiverMetaData( cls);
            default:
                return null;
        }
    }

    /**
     * 获取manifest里面的值
     * @param key manifest字段
     * @return 截取后的key
     */
    public static String getValueWithSubString(String key){

        String str = App.get().METADATA.getString(key,"");
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(str)){
            str = str.substring(1, str.length());
        }
        return str;
    }

    /**
     * 获取所有权限
     */
    public static String[] getAppPermission(){
        PackageInfo info = null;
        try {
            info = App.get().getPackageManager().
                    getPackageInfo(App.get().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        return info.requestedPermissions;
    }
}
