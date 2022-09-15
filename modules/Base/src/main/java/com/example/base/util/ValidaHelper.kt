package com.example.base.util

import android.annotation.SuppressLint
import android.text.InputFilter
import android.text.TextUtils
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import java.util.regex.Pattern

object ValidaHelper {

    private val TAG: String = ValidaHelper::class.java.simpleName

    val phonePattern: Pattern = Pattern.compile("^1([3-9])[0-9]{9}$")

    val passwordPattern: Pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!\"#\$%&'()*+,-./:;<=>?@\\[\\]\\\\^_`{|}~])[a-zA-Z0-9!\"#\$%&'()*+,-./:;<=>?@\\[\\]\\\\^_`{|}~]{8,16}$")

    val verifyCodePattern: Pattern = Pattern.compile("\\d{6}")

    /**
     * 手机号长度限制
     */
    val phoneLenInputFilter = InputFilter.LengthFilter(11)

    /**
     * 密码长度限制
     */
    val passwordLenInputFilter = InputFilter.LengthFilter(16)

    /**
     * 验证码长度限制
     */
    val msmCodeLenInputFilter = InputFilter.LengthFilter(6)

    @JvmStatic
    fun validatePhone(phone: String?): Boolean {
        return !TextUtils.isEmpty(phone) && phonePattern.matcher(phone!!).matches()
    }

    @JvmStatic
    fun validatePhoneLen(phone: String?): Boolean {
        return !TextUtils.isEmpty(phone) && phone!!.length == 11
    }

    @JvmStatic
    fun validatePassword(password: String?): Boolean {
        return !TextUtils.isEmpty(password) && passwordPattern.matcher(password!!).matches()
    }

    @JvmStatic
    fun validatePasswordLen(password: String?): Boolean {
        return !TextUtils.isEmpty(password) && password!!.length >= 8 && password.length <= 16
    }

    @JvmStatic
    fun validateCode(code: String?): Boolean {
        return !TextUtils.isEmpty(code) && verifyCodePattern.matcher(code!!).matches()
    }

    /**
     * 验证数字是否小于0
     */
    @JvmStatic
    fun validateLtZero(num: Int): Boolean {
        return num <= 0
    }


    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun noCopy(editText: EditText) {
        editText.setOnLongClickListener { true }
        editText.isLongClickable = false
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // setInsertionDisabled when user touches the view
                try {
                    val editorField = TextView::class.java.getDeclaredField("mEditor")
                    editorField.isAccessible = true
                    val editorObject = editorField[editText]

                    // if this view supports insertion handles
                    val editorClass = Class.forName("android.widget.Editor")
                    val mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled")
                    mInsertionControllerEnabledField.isAccessible = true
                    mInsertionControllerEnabledField[editorObject] = false

                    // if this view supports selection handles
                    val mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled")
                    mSelectionControllerEnabledField.isAccessible = true
                    mSelectionControllerEnabledField[editorObject] = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            false
        }
        editText.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.clear()
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
        }
    }
}