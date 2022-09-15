package com.example.base.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.base.activity.listener.BaseLifecycleListener
import com.example.base.app.App
import com.example.base.jetpack.binding.DataBindingFragment
import com.example.base.util.UIUtil

abstract class CommonFragment : DataBindingFragment() {
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null
    private var mAnimationLoaded = false
    private var mStateEnable = false
    private var onEnableDo: (() -> Unit)? = null

    protected fun <T : ViewModel> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    protected fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(mActivity)
        }
        return mActivityProvider!![modelClass]
    }

    protected fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider = ViewModelProvider((mActivity.applicationContext as App), getApplicationFactory(mActivity))
        }
        return mApplicationProvider!![modelClass]
    }

    private fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    private fun checkActivity(fragment: Fragment) {
        fragment.activity ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    protected open fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }

    protected fun isCurrentPage(resId: Int): Boolean {
        return try {
            nav().currentDestination == nav().graph.findNode(resId)
        } catch (e: Exception) {
            false
        }
    }

    protected fun navigatePush(resId: Int, args: Bundle) {
        try {
            nav().navigate(resId, args)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun navigatePush(resId: Int) {
        try {
            nav().navigate(resId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun navigatePop() {
        try {
            nav().navigateUp()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        //TODO 错开动画转场与 UI 刷新的时机，避免掉帧卡顿的现象
        HANDLER.postDelayed({
            if (!mAnimationLoaded) {
                mAnimationLoaded = true
                loadInitData()
            }
        }, 280)
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    open fun loadInitData() {}

    protected fun toggleSoftInput() {
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
    }

    protected fun openUrlInBrowser(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    protected fun showSoftKeyboard() {
        val focusView = view?.findFocus()
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (focusView != null) {
            imm.showSoftInput(focusView, 0)
        }
    }

    protected fun showSoftKeyboard(view: View) {
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view.requestFocus()) {
            imm.showSoftInput(view, 0)
        }
    }

    protected fun hideSoftKeyboard(notRequest: Boolean = false) {
        val focusView = view?.findFocus()
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        UIUtil.fullScreen(requireActivity())
        if (focusView != null) {
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
        } else if (!notRequest) {
            view?.requestFocus()
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    open fun getLifecycleListener(): BaseLifecycleListener {
        return BaseLifecycleListener()
    }

    open fun whenEnableDo(onEnable: () -> Unit) {
        if (isStateEnable()) onEnable()
        else onEnableDo = onEnable
    }

    override fun onStart() {
        super.onStart()
        mStateEnable = true
    }

    override fun onResume() {
        super.onResume()
        mStateEnable = true
        onEnableDo?.invoke()
        onEnableDo = null
        hideSoftKeyboard(notRequest = true)
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(notRequest = true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mStateEnable = false
        super.onSaveInstanceState(outState)
    }

    /**
     * 页面不显示时, 移除焦点
     */
    override fun onStop() {
        mStateEnable = false
        hideSoftKeyboard()
        super.onStop()
    }

    protected fun back() {
        try {
            if (requireActivity().supportFragmentManager.backStackEntryCount == 0) {
                requireActivity().finish()
            } else {
                requireActivity().supportFragmentManager.popBackStack()
            }
        } catch (e: Exception) {
            activity?.finish()
        }
    }

    protected fun isStateEnable(): Boolean = mStateEnable

    companion object {
        private val HANDLER = Handler()
    }
}