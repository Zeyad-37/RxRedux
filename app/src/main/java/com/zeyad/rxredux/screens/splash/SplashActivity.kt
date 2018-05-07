package com.zeyad.rxredux.screens.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zeyad.rxredux.screens.user.list.UserListActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(UserListActivity.getCallingIntent(this))
        finish()
    }
}
