package com.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.base.app.App
import com.example.base.util.HandlerUtil
import com.example.main.MainActivity
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity(), CancelAdapt {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.get(this)
        if (App.get() == null) exitProcess(0)
        HandlerUtil.post { openMainActivity() }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, R.anim.slide_null)
    }
}