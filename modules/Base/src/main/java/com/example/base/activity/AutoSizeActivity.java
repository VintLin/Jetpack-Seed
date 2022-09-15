package com.example.base.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import org.jetbrains.annotations.NotNull;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.internal.CancelAdapt;

public class AutoSizeActivity extends AppCompatActivity implements CancelAdapt {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public Resources getResources() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            float width = (float) AutoSizeConfig.getInstance().getScreenWidth();
            float height = (float) AutoSizeConfig.getInstance().getScreenHeight();
            boolean isBaseOnWidth;
            float sizeInDp;
            if (height < width) {
                isBaseOnWidth = width / 640f < height / 360f;
                sizeInDp = isBaseOnWidth ? 640f : 360f;
            } else {
                isBaseOnWidth = width / 360f < height / 640f;
                sizeInDp = isBaseOnWidth ? 360f : 640f;
            }
            AutoSizeCompat.autoConvertDensity(super.getResources(), sizeInDp, isBaseOnWidth);
        }
        return super.getResources();
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) AutoSize.autoConvertDensity(this, 360f, true);
    }
}
