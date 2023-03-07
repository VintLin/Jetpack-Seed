package com.example.main.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.example.main.R
import kotlinx.android.synthetic.main.layout_tab_button.view.*

class TabButtonView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var tab: TabButtonData? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_tab_button, this, true)
    }

    fun refresh(tab: TabButtonData) {
        this.tab = tab
        tab_background.setStatus(tab.isChoice, tab.isFirst, tab.isEnd)
        val layoutParams = if (tab.isChoice) FrameLayout.LayoutParams(Constant.getActiveButtonWidth(), Constant.getActiveButtonHeight())
        else FrameLayout.LayoutParams(Constant.getButtonWidth(), Constant.getButtonHeight())
        tab_background.layoutParams = layoutParams
        title_text_view.text = tab.text
        icon_image_view.visibility = if (tab.isChoice) View.VISIBLE else View.GONE
    }
}
