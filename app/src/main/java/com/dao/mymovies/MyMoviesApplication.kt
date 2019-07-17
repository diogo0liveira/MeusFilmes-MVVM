package com.dao.mymovies

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class MyMoviesApplication : DaggerApplication()
{
    private lateinit var injector: AndroidInjector<MyMoviesApplication>

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>
    {
        return injector
    }
}