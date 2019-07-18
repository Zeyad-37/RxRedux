package com.zeyad.rxredux.screens.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zeyad.rxredux.R
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        setSupportActionBar(toolbar)
    }

}
