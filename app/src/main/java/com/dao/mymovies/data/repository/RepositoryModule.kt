package com.dao.mymovies.data.repository

import com.dao.mymovies.data.MovieRepository
import com.dao.mymovies.data.local.MoviesLocalDataSource
import com.dao.mymovies.data.local.LocalDataSourceModule
import com.dao.mymovies.data.remote.MoviesRemoteDataSource
import com.dao.mymovies.data.remote.RemoteDataSourceModule
import dagger.Module
import dagger.Provides

/**
 * Created in 07/05/19 11:45.
 *
 * @author Diogo Oliveira.
 */
@Module(includes = [LocalDataSourceModule::class, RemoteDataSourceModule::class])
class RepositoryModule
{
    @Provides
    fun provideMovieRepository(local: MoviesLocalDataSource, remote: MoviesRemoteDataSource): MovieRepository
    {
        return MoviesRepository(local, remote)
    }
}