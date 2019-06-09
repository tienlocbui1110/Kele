package com.lh.kete.activity.pref

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lh.kete.R

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingFragment()).commit()
        setContentView(R.layout.activity_setting)
    }
}
