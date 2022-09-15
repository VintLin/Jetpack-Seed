package com.example.base.util;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;

public class UIUtil {
    private static final int FLAG_NOTCH_SUPPORT = 0x00010000;
    private static final String DISPLAY_NOTCH_STATUS = "display_notch_status";

    public static void fullScreen(Activity activity) {
        if (activity == null) return;
        int enabledOverlays = View.SYSTEM_UI_FLAG_VISIBLE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        activity.getWindow().getDecorView().setSystemUiVisibility(enabledOverlays);
    }

    public static int float2Int(float f) {
        return (int) (f + 0.5F);
    }

    public static void dealHuaweiNotch(Activity activity) {
        dealHuaweiNotch(activity, activity.getWindow());
    }

    public static void dealHuaweiNotch(Context context, Window window) {
        if (!hasNotchInScreenAtHuawei(context)) return; // 没有华为刘海不处理
        if (isHuaweiNotchClose(context)) return; //  刘海关闭不处理

        setHuaweiActivityFull(window);
    }

    private static boolean isHuaweiDevice() {
        String brand = Build.BRAND.trim().toUpperCase(Locale.ENGLISH);
        return brand.contains("HUAWEI") || brand.contains("HONOR");
    }

    /**
     * 华为start
     */
    // 判断是否是华为刘海屏
    private static boolean hasNotchInScreenAtHuawei(Context context) {
        if (!isHuaweiDevice()) return false; // 不是华为手机
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (Boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 华为刘海屏展示是否关闭
     */
    private static boolean isHuaweiNotchClose(Context context) {
        int isNotchSwitchOpen = 0;
        try {
            isNotchSwitchOpen = Settings.Secure.getInt(context.getContentResolver(), DISPLAY_NOTCH_STATUS, 0);
        } catch (Exception e) {
        }
        return isNotchSwitchOpen == 1;
    }

    private static void setHuaweiActivityFull(Window window) {
        try {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
        }
    }
}
