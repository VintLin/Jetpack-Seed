package com.example.main.widget.tabs

import com.example.base.app.App
import me.jessyan.autosize.utils.AutoSizeUtils

object Constant {
    const val ACTIVE_BUTTON_COLOR = "#8DFBFF"
    const val ACTIVE_INNER_SHADOW = "#D4FFFF"
    const val ACTIVE_GRADIENT_COLOR = "#DBFFFA"

    const val BUTTON_COLOR = "#62E7FF"
    const val INNER_SHADOW = "#BAFEFF"
    const val GRADIENT_COLOR = "#9AFEFF"

    const val BORDER_COLOR = "#57CEF1"
    const val SHADOW_COLOR = "#3AB8F3"

    fun getActiveButtonWidth(): Int = AutoSizeUtils.dp2px(App.get().baseContext, 236f)
    fun getActiveButtonHeight(): Int = AutoSizeUtils.dp2px(App.get().baseContext, 86f)
    fun getButtonWidth(): Int = AutoSizeUtils.dp2px(App.get().baseContext, 168f)
    fun getButtonHeight(): Int = AutoSizeUtils.dp2px(App.get().baseContext, 78f)

    fun getHorizontalWidth(): Float {
        return AutoSizeUtils.dp2px(App.get().baseContext, 8f).toFloat()
    }

    fun getVerticalWidth(): Float {
        return AutoSizeUtils.dp2px(App.get().baseContext, 6f).toFloat()
    }

    fun getBorderWidth(): Float {
        return AutoSizeUtils.dp2px(App.get().baseContext, 3f).toFloat()
    }

    fun getConnectWidth(): Float {
        return AutoSizeUtils.dp2px(App.get().baseContext, 20f).toFloat()
    }

    fun getConnectExtend(): Float {
        return AutoSizeUtils.dp2px(App.get().baseContext, 2f).toFloat()
    }
}