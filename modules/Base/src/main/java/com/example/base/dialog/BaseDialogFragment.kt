package com.example.base.dialog

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.base.dialog.BaseDialog
import com.example.base.util.UIUtil
import kotlin.math.max
import kotlin.math.min


abstract class BaseDialogFragment<T : BaseDialogFragment.Setting> : DialogFragment(),
    DialogInterface.OnKeyListener {
    companion object {
        private const val TAG: String = "BaseDialogFragment"
        private const val ARGUMENT_SETTING_KEY: String = "BaseDialogFragment.ARGUMENT_SETTING_KEY"
    }

    protected lateinit var setting: T

    private var mStateEnable = false
    private var dismissWhenEnable = false
    private var animator: ValueAnimator? = null

    /**
     * 在该状态为false时 操作UI会导致程序奔溃
     */
    protected fun isStateEnable(): Boolean = mStateEnable

    open fun initSetting(setting: T) {
        this.setting = setting
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.apply {
            // Fragment 状态恢复使用
            try {
                val setting: Any? = getParcelable(ARGUMENT_SETTING_KEY)
                if (setting != null && !this@BaseDialogFragment::setting.isInitialized) {
                    this@BaseDialogFragment.setting = setting as T
                }
            } catch (e: Exception) {
                // 类型转化异常
                e.printStackTrace()
            }
        }

        // 添加默认值避免lateinit异常
        if (!::setting.isInitialized) {
            setting = requireDefaultSetting()
        }
    }

    override fun onStart() {
        super.onStart()
        mStateEnable = true
        dialog?.window?.decorView?.setPadding(0, 0, 0, 0)
    }

    override fun onResume() {
        super.onResume()
        mStateEnable = true
        if (dismissWhenEnable) {
            dismissWhenEnable = true
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mStateEnable = false
        super.onSaveInstanceState(outState)
        if (::setting.isInitialized) {
            outState.putParcelable(BaseDialog.ARGUMENT_SETTING_KEY, setting)
        }
    }

    override fun onStop() {
        mStateEnable = false
        super.onStop()
    }

    override fun dismiss() {
        if (isStateEnable()) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        beforeShowDialog(dialog)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        beforeCreateDialog(dialog)
        beforeShowDialog(dialog)
        return dialog
    }

    /**
     * 以下操作可以防止部分设备系统导航栏弹出的情况
     */
    private fun beforeShowDialog(dialog: Dialog?) {
        // 1. 在未展示弹窗之前先不聚焦
        dialog?.window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
        }

        dialog?.setOnShowListener {
            dialog.window?.apply {
                // 2. 展示弹窗后即可设置聚焦于其上
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
                decorView.systemUiVisibility = uiOptions
            }
        }
    }

    /**
     * 保证弹窗全屏显示
     */
    private fun beforeCreateDialog(dialog: Dialog?) {
        dialog?.apply {
            window?.apply {
                val layoutParams: WindowManager.LayoutParams = attributes
                decorView.setPadding(0, 0, 0, 0)
                decorView.background = ColorDrawable(Color.TRANSPARENT)
                layoutParams.dimAmount = setting.backgroundAlpha
                layoutParams.alpha = setting.alpha
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.TRANSPARENT
                } else {
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
                if (setting.fullscreen) {
                    val rootWidth = getRootWidth()
                    val rootHeight = getRootHeight()
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        layoutParams.width = min(rootWidth, rootHeight)
                        layoutParams.height = max(rootWidth, rootHeight)
                    } else {
                        layoutParams.width = max(rootWidth, rootHeight)
                        layoutParams.height = min(rootWidth, rootHeight)
                    }
                } else {
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                }
                if (setting.showKeyboard) {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    keyboard(setting.showKeyboard)
                } else {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutParams.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                attributes = layoutParams
                UIUtil.dealHuaweiNotch(requireActivity())
                decorView.apply {
                    viewTreeObserver.addOnGlobalLayoutListener {
                        onGlobalLayoutChange()
                    }
                }
            }
            setCancelable(setting.cancelable)
            setOnKeyListener(this@BaseDialogFragment)
            setCanceledOnTouchOutside(setting.touchOutsideCancel)
        }
    }

    fun onGlobalLayoutChange() {
        val rect = Rect()
        dialog?.window?.decorView?.apply {
            getWindowVisibleDisplayFrame(rect)
            val goneHeight = height - rect.bottom
            if (goneHeight > 100) {
                val location = IntArray(2)
                rootView.getLocationInWindow(location)
                val moveHeight: Int = location[1] + rootView.height - rect.bottom
                setPaddingRelative(paddingStart, paddingTop, paddingEnd, moveHeight)
            } else {
                setPaddingRelative(paddingStart, paddingTop, paddingEnd, 0)
            }
        }
    }


    override fun onDestroy() {
        animator?.cancel()
        animator?.removeAllListeners()
        animator = null
        setting.onSubEvent = null
        setting.onMainEvent = null
        setting.onCloseEvent = null
        setting.onDialogDismiss = null
        setting.onDialogCreated = null
        super.onDestroy()
    }

    open fun keyboard(isShow: Boolean) {
        try {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (isShow) {
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
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
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun showSoftKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view.requestFocus()) {
            imm.showSoftInput(view, 0)
        }
    }

    protected fun hideSoftKeyboard() {
        val focusView = view?.findFocus()
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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
                layoutParams.dimAmount = setting.backgroundAlpha * (it?.animatedValue as Float)
                this.attributes = layoutParams
            }
            animator.start()
        }
    }

    open fun show(manager: FragmentManager) {
        super.show(manager, TAG)
    }

    open fun show(activity: FragmentActivity) {
        super.show(activity.supportFragmentManager, TAG)
    }

    protected open fun getRootWidth(): Int {
        val displayWidth: Int
        val display = requireActivity().windowManager.defaultDisplay
        val point = Point()
        displayWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point)
            point.x
        } else {
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(dm)
            dm.widthPixels
        }
        return displayWidth
    }

    protected open fun getRootHeight(): Int {
        var displayHeight: Int
        val display = requireActivity().windowManager.defaultDisplay
        val point = Point()
        displayHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point)
            point.y
        } else {
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(dm)
            dm.heightPixels
        }
        return displayHeight
    }

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun requireDefaultSetting(): T

    interface OnEventListener {
        fun onEvent(dialog: BaseDialogFragment<*>)
    }

    interface OnResultListener {
        fun onResult(dialog: BaseDialogFragment<*>): Boolean
    }

    open class Setting() : Parcelable {
        /**
         * 标题
         */
        var title: String = ""

        /**
         * 窗口背景透明度
         */
        var backgroundAlpha: Float = 0.5f

        /**
         * 窗口透明度
         */
        var alpha: Float = 1.0f

        /**
         * 是否需要显示键盘
         */
        var showKeyboard: Boolean = false

        /**
         * 是否展示关闭按钮
         */
        var showCloseButton: Boolean = true

        /**
         * 是否可点击弹窗外围取消
         */
        var fullscreen: Boolean = false

        /**
         * 是否可点击弹窗外围取消
         */
        var touchOutsideCancel: Boolean = false

        /**
         * 是否可以取消
         */
        var cancelable: Boolean = false

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

        constructor(parcel: Parcel) : this() {
            title = parcel.readString() ?: ""
            backgroundAlpha = parcel.readFloat()
            alpha = parcel.readFloat()
            showKeyboard = parcel.readByte() != 0.toByte()
            showCloseButton = parcel.readByte() != 0.toByte()
            fullscreen = parcel.readByte() != 0.toByte()
            touchOutsideCancel = parcel.readByte() != 0.toByte()
            cancelable = parcel.readByte() != 0.toByte()
            subText = parcel.readString() ?: ""
            mainText = parcel.readString() ?: ""
        }

        fun isShowSubButton(): Boolean = subText.isNotEmpty() && subText.isNotBlank()
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeFloat(backgroundAlpha)
            parcel.writeFloat(alpha)
            parcel.writeByte(if (showKeyboard) 1 else 0)
            parcel.writeByte(if (showCloseButton) 1 else 0)
            parcel.writeByte(if (fullscreen) 1 else 0)
            parcel.writeByte(if (touchOutsideCancel) 1 else 0)
            parcel.writeByte(if (cancelable) 1 else 0)
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

    abstract class Builder {
        fun title(title: String): Builder {
            setting().title = title
            return this
        }

        fun cancelable(cancelable: Boolean): Builder {
            setting().cancelable = cancelable
            return this
        }

        fun fullscreen(fullscreen: Boolean): Builder {
            setting().fullscreen = fullscreen
            return this
        }

        fun touchOutsideCancel(touchOutsideCancel: Boolean): Builder {
            setting().touchOutsideCancel = touchOutsideCancel
            return this
        }

        fun backgroundAlpha(backgroundAlpha: Float): Builder {
            setting().backgroundAlpha = backgroundAlpha
            return this
        }

        fun alpha(alpha: Float): Builder {
            setting().alpha = alpha
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

        fun show(manager: FragmentManager) {
            build().show(manager, manager::class.java.name)
        }

        fun show(activity: FragmentActivity) {
            build().show(activity.supportFragmentManager, activity::class.java.name)
        }

        protected abstract fun setting(): Setting

        protected abstract fun build(): BaseDialogFragment<*>
    }

}