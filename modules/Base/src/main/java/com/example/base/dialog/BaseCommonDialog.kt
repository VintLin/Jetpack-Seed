package com.example.base.dialog

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 在Fragment状态恢复的时候需要一个无参的构造方法
 */
abstract class BaseCommonDialog<T : BaseCommonDialog.Setting> : BaseDialog<T>() {
    companion object {
        private const val TAG: String = "BaseDialogFragment"
    }

    private var dismissWhenEnable = false
    private var animator: ValueAnimator? = null

    @Deprecated("Deprecated in Java")
    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        setting.onDialogShowed?.onEvent(this)
    }

    override fun onResume() {
        super.onResume()
        if (dismissWhenEnable) {
            dismissWhenEnable = true
            dismiss()
        }
    }

    override fun dismiss() {
        if (setting.isDismissed) return
        if (isStateEnable()) {
            setting.isDismissed = true
            setting.onDialogDismiss?.onEvent(this)
            super.dismiss()
        } else {
            dismissWhenEnable = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setting.onDialogCreated?.onEvent(this)
        return inflater.inflate(layoutId(), container)
    }

    override fun onDestroy() {
        try {
            animator?.cancel()
            animator?.removeAllListeners()
            animator = null
            setting.onSubEvent = null
            setting.onMainEvent = null
            setting.onCloseEvent = null
            setting.onDialogDismiss = null
            setting.onDialogCreated = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    open fun keyboard(isShow: Boolean) {
        try {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (isShow) {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
            } else {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            return !setting.cancelable
        } else {
            false
        }
    }

    protected fun showSoftKeyboard() {
        val focusView = view?.findFocus()
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun showSoftKeyboard(view: View) {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view.requestFocus()) {
            imm.showSoftInput(view, 0)
        }
    }

    protected fun hideSoftKeyboard() {
        val focusView = view?.findFocus()
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            focusView.clearFocus()
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
        } else {
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    fun fadeBackground() {
        dialog?.window?.apply {
            val animator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)
            animator.setTarget(this)
            animator.duration = 200
            animator.addUpdateListener {
                val layoutParams = this.attributes
                layoutParams.dimAmount = setting.dimAmount * (it?.animatedValue as Float)
                this.attributes = layoutParams
            }
            animator.start()
        }
    }

    open fun show(manager: FragmentManager) {
        super.show(manager, TAG)
    }

    @LayoutRes
    abstract override fun layoutId(): Int

    interface OnEventListener {
        fun onEvent(dialog: BaseCommonDialog<*>)
    }

    interface OnResultListener {
        fun onResult(dialog: BaseCommonDialog<*>): Boolean
    }

    open class Setting : BaseDialog.Setting {

        /**
         * 标题
         */
        var title: String = ""

        /**
         * 是否展示关闭按钮
         */
        var showCloseButton: Boolean = true

        /**
         * 弹窗是否执行Dismiss
         */
        var isDismissed: Boolean = false

        // 副按钮文案
        var subText: String = ""

        var onSubEvent: OnEventListener? = null

        // 主按钮文案
        var mainText: String = ""

        var onMainEvent: OnResultListener? = null

        // 点击关闭按钮事件
        var onCloseEvent: OnEventListener? = null

        var onDialogDismiss: OnEventListener? = null

        var onDialogCreated: OnEventListener? = null

        var onDialogShowed: OnEventListener? = null

        constructor() : super()

        constructor(parcel: Parcel) : super(parcel) {
            title = parcel.readString() ?: ""
            showCloseButton = parcel.readByte() != 0.toByte()
            isDismissed = parcel.readByte() != 0.toByte()
            subText = parcel.readString() ?: ""
            mainText = parcel.readString() ?: ""

        }

        fun isShowSubButton(): Boolean = subText.isNotEmpty() && subText.isNotBlank()

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeString(title)
            parcel.writeByte(if (showCloseButton) 1 else 0)
            parcel.writeByte(if (isDismissed) 1 else 0)
            parcel.writeString(subText)
            parcel.writeString(mainText)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Setting> {
            override fun createFromParcel(parcel: Parcel): Setting {
                return Setting(parcel)
            }

            override fun newArray(size: Int): Array<Setting?> {
                return arrayOfNulls(size)
            }
        }
    }

    abstract class Builder : BaseDialog.Builder<BaseCommonDialog<*>>() {
        fun title(title: String): Builder {
            setting().title = title
            return this
        }

        fun showKeyboard(showKeyboard: Boolean): Builder {
            setting().showKeyboard = showKeyboard
            return this
        }

        fun showCloseButton(showCloseButton: Boolean): Builder {
            setting().showCloseButton = showCloseButton
            return this
        }

        fun onCloseEvent(onCloseEvent: OnEventListener): Builder {
            setting().onCloseEvent = onCloseEvent
            return this
        }

        open fun setDefaultSubButton(): Builder {
            setting().subText = "取消"
            return this
        }

        open fun setSubButton(text: String): Builder {
            setting().subText = text
            return this
        }

        open fun setSubButton(onSubEvent: OnEventListener): Builder {
            return setSubButton("取消", onSubEvent)
        }

        open fun setSubButton(text: String, onSubEvent: OnEventListener): Builder {
            setting().subText = text
            setting().onSubEvent = onSubEvent
            return this
        }

        open fun setMainButton(text: String): Builder {
            setting().mainText = text
            return this
        }

        open fun setMainButton(onMainEvent: OnResultListener): Builder {
            return setMainButton("确定", onMainEvent)
        }

        open fun setMainButton(text: String, onMainEvent: OnResultListener): Builder {
            setting().mainText = text
            setting().onMainEvent = onMainEvent
            return this
        }

        fun onDialogDismiss(onDialogDismiss: OnEventListener): Builder {
            setting().onDialogDismiss = onDialogDismiss
            return this
        }

        fun onDialogCreated(onDialogCreated: OnEventListener): Builder {
            setting().onDialogCreated = onDialogCreated
            return this
        }

        fun onDialogShowed(onDialogShowed: OnEventListener): Builder {
            setting().onDialogShowed = onDialogShowed
            return this
        }

        abstract override fun setting(): Setting

        abstract override fun build(): BaseCommonDialog<*>
    }
}