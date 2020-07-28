package com.sunasterisk.boringweather.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sunasterisk.boringweather.R

class MainActivity : AppCompatActivity(), MainContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
