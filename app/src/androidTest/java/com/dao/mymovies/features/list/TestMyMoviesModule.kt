@file:Suppress("unused")

package com.dao.mymovies.features.list

import androidx.lifecycle.ViewModel
import com.dao.mymovies.data.repository.FakeRepositoryModule
import com.dao.mymovies.di.annotations.ActivityScoped
import com.dao.mymovies.di.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created in 15/08/18 17:04.
 *
 * @author Diogo Oliveira.
 */
@Module(includes = [FakeRepositoryModule::class])
abstract class TestMyMoviesModule
{
    @Binds
    @IntoMap
    @ActivityScoped
    @ViewModelKey(MyMoviesViewModel::class)
    abstract fun bindViewModel(viewModel: MyMoviesViewModel): ViewModel
}