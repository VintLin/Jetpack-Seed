package com.example.base.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.base.util.UIUtil
import kotlin.math.max
import kotlin.math.min

abstract class BaseDialog<T : BaseDialog.Setting> : DialogFragment(), DialogInterface.OnKeyListener,
    View.OnSystemUiVisibilityChangeListener {

    companion object {
        const val ARGUMENT_SETTING_KEY = "BaseDialog.ARGUMENT_SETTING_KEY"
    }
    protected lateinit var setting: T

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun requireDefaultSetting(): T

    private var mStateEnable = false

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
                if (setting != null && !this@BaseDialog::setting.isInitialized) {
                    this@BaseDialog.setting = setting as T
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


    override fun onResume() {
        super.onResume()
        mStateEnable = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mStateEnable = false
        super.onSaveInstanceState(outState)
        if (::setting.isInitialized) {
            outState.putParcelable(ARGUMENT_SETTING_KEY, setting)
        }
    }

    override fun onStop() {
        mStateEnable = false
        super.onStop()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        beforeCreateDialog(dialog)
        beforeShowDialog(dialog)
        return dialog
    }

    protected fun beforeCreateDialog(dialog: Dialog?) {
        dialog?.apply {
            window?.apply {
                val layoutParams: WindowManager.LayoutParams = attributes
                decorView.setPadding(0, 0, 0, 0)
                // 设置窗口透明
                decorView.background = ColorDrawable(Color.TRANSPARENT)
                layoutParams.dimAmount = setting.dimAmount
                layoutParams.alpha = setting.alpha
                // 是否需要输入
                if (setting.showKeyboard) {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                } else {
                    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                }

                // 5.0以上透明状态栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.TRANSPARENT
                } else {
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }

                // 是否全屏
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

                // 显示区域延伸至刘海屏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                attributes = layoutParams
                // 处理华为刘海屏
                UIUtil.dealHuaweiNotch(requireActivity())
            }
            setCancelable(setting.cancelable)
            setOnKeyListener(this@BaseDialog)
            setCanceledOnTouchOutside(setting.touchOutsideCancel)
        }
    }

    /**
     * 以下操作可以防止部分设备系统导航栏弹出的情况
     * @suppress 不使用dialog的 onCancelListener, onShowListener 和 onDismissListener 方法，容易出现内存泄漏
     */
    protected fun beforeShowDialog(dialog: Dialog?) {
        // 1. 在未展示弹窗之前先不聚焦
        dialog?.window?.apply {
            setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        // 设置系统状态栏监听
        dialog?.window?.decorView?.setOnSystemUiVisibilityChangeListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId(), container)


    override fun onSystemUiVisibilityChange(visibility: Int) {
        hideSystemUi()
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.setPadding(0, 0, 0, 0)
        hideSystemUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beforeShowDialog(dialog)
    }

    override fun onDestroyView() {
        // dialog 销毁前移除监听， 避免内存泄漏
        this.dialog?.setOnKeyListener(null)
        dialog?.window?.decorView?.setOnSystemUiVisibilityChangeListener(null)
        super.onDestroyView()
    }

    protected fun hideSystemUi() {
        dialog?.window?.apply {
            // 2. 展示弹窗后即可设置聚焦于其上, 显示的时候取消标志
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            decorView.systemUiVisibility = uiOptions
        }
    }

    protected open fun getRootWidth(): Int {
        val display = requireActivity().windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        return point.x
    }

    protected open fun getRootHeight(): Int {
        val display = requireActivity().windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        return point.y
    }

    fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, javaClass.simpleName)
    }

    open class Setting() : Parcelable {

        /**
         * 是否全屏
         */
        var fullscreen: Boolean = false

        /**
         * 是否可以取消
         */
        var cancelable: Boolean = false

        /**
         * 是否可点击弹窗外围取消
         */
        var touchOutsideCancel: Boolean = false

        /**
         * 窗口背景透明度
         */
        var alpha: Float = 1.0f

        /**
         * Dialog背景透明度
         */
        var dimAmount: Float = 0.5f

        /**
         * 是否需要显示键盘
         */
        var showKeyboard: Boolean = false

        constructor(parcel: Parcel) : this() {
            fullscreen = parcel.readByte() != 0.toByte()
            cancelable = parcel.readByte() != 0.toByte()
            touchOutsideCancel = parcel.readByte() != 0.toByte()
            alpha = parcel.readFloat()
            dimAmount = parcel.readFloat()
            showKeyboard = parcel.readByte() != 0.toByte()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeByte(if (fullscreen) 1 else 0)
            parcel.writeByte(if (cancelable) 1 else 0)
            parcel.writeByte(if (touchOutsideCancel) 1 else 0)
            parcel.writeFloat(alpha)
            parcel.writeFloat(dimAmount)
            parcel.writeByte(if (showKeyboard) 1 else 0)
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

    abstract class Builder<out T : BaseDialog<*>> {

        open fun fullscreen(fullscreen: Boolean): Builder<T> {
            setting().fullscreen = fullscreen
            return this
        }

        open fun cancelable(cancelable: Boolean): Builder<T> {
            setting().cancelable = cancelable
            return this
        }

        open fun touchOutsideCancel(touchOutsideCancel: Boolean): Builder<T> {
            setting().touchOutsideCancel = touchOutsideCancel
            return this
        }

        open fun alpha(alpha: Float): Builder<T> {
            setting().alpha = alpha
            return this
        }

        open fun backgroundAlpha(dimAmount: Float): Builder<T> {
            setting().dimAmount = dimAmount
            return this
        }

        /**
         * [onSaveInstanceState]
         * 调用时机：
         * 1、当用户按下HOME键时。
         * 2、从最近应用中选择运行其他的程序时
         * 3、按下电源按键（关闭屏幕显示）时。
         * 4、从当前activity启动一个新的activity时
         * 5、屏幕方向切换时(无论竖屏切横屏还是横屏切竖屏都会调用)
         *
         * 展示Dialog时，若activity调用了 [onSaveInstanceState] 事，此时展示Dialog会导致崩溃
         */
        fun show(manager: FragmentManager) {
            try {
                build().show(manager, manager::class.java.name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun show(activity: FragmentActivity) {
            try {
                build().show(activity.supportFragmentManager, activity::class.java.name)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        protected abstract fun setting(): Setting

        protected abstract fun build(): T
    }

}