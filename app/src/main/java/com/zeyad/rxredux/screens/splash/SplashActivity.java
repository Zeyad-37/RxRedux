package com.zeyad.rxredux.screens.splash;

import com.zeyad.rxredux.screens.user.list.UserListActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(UserListActivity.getCallingIntent(this));
        finish();
    }
}
