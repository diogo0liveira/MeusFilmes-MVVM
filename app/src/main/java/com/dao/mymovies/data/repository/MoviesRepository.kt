package com.dao.mymovies.data.repository

import androidx.paging.DataSource
import com.dao.mymovies.data.MovieRepository
import com.dao.mymovies.data.local.MoviesLocalDataSource
import com.dao.mymovies.data.remote.MoviesRemoteDataSource
import com.dao.mymovies.model.Movie
import com.dao.mymovies.pojo.SearchResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created in 03/08/18 12:30.
 *
 * @author Diogo Oliveira.
 */
@Singleton
class MoviesRepository @Inject constructor(
        local: MoviesLocalDataSource,
        remote: MoviesRemoteDataSource):
        Repository<MoviesLocalDataSource, MoviesRemoteDataSource>(local, remote), MovieRepository
{
    override fun save(movie: Movie): Completable = local.save(movie)

    override fun delete(movie: Movie) = local.delete(movie)

    override fun getMovies(): DataSource.Factory<Int, Movie> = local.getMovies()

    override fun isFavorite(movie: Movie): Single<Boolean> = local.isFavorite(movie)

    override fun search(query: String, page: Int): Observable<Response<SearchResult>> = remote.search(query, page)
}