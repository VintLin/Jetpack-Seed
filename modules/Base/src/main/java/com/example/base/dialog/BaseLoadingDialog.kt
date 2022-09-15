package com.example.base.dialog

import android.os.Bundle
import androidx.annotation.LayoutRes
import com.example.base.dialog.BaseDialog

abstract class BaseLoadingDialog : BaseDialog<BaseDialog.Setting>() {

    open var hasMask: Boolean = false

    override fun requireDefaultSetting(): Setting = Setting()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initSetting(Setting().apply {
                alpha = 1.0f
                dimAmount = if (hasMask) 0.5f else 0f
                cancelable = false
                touchOutsideCancel = false
                showKeyboard = false
                fullscreen = true
            })
        }
    }


    @LayoutRes
    abstract override fun layoutId(): Int

}
