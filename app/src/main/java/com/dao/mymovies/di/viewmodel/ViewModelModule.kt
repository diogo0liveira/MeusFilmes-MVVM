@file:Suppress("unused")

package com.dao.mymovies.di.viewmodel

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

/**
 * Created in 06/08/19 16:49.
 *
 * @author Diogo Oliveira.
 */
@Module
abstract class ViewModelModule
{
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}