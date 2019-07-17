package com.dao.mymovies.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection

@SuppressLint("Registered")
class BaseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}