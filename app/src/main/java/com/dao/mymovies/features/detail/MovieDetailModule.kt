@file:Suppress("unused")

package com.dao.mymovies.features.detail

import androidx.lifecycle.ViewModel
import com.dao.mymovies.data.repository.RepositoryModule
import com.dao.mymovies.di.annotations.ActivityScoped
import com.dao.mymovies.di.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created in 17/08/18 11:36.
 *
 * @author Diogo Oliveira.
 */
@Module(includes = [RepositoryModule::class])
abstract class MovieDetailModule
{
    @Binds
    @IntoMap
    @ActivityScoped
    @ViewModelKey(MovieDetailViewModel::class)
    abstract fun bindViewModel(viewModel: MovieDetailViewModel): ViewModel
}