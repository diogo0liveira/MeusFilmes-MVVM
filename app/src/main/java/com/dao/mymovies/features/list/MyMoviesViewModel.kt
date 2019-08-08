package com.dao.mymovies.features.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dao.mymovies.data.MovieRepository
import com.dao.mymovies.model.Movie
import com.dao.mymovies.model.Order
import javax.inject.Inject

/**
 * Created in 06/08/19 16:53.
 *
 * @author Diogo Oliveira.
 */
class MyMoviesViewModel @Inject constructor(repository: MovieRepository): ViewModel()
{
    private val movies: LiveData<PagedList<Movie>>
    private var order: Order = Order.TITLE

    init
    {
        val config = PagedList.Config.Builder().setPageSize(30).build()
        movies = LivePagedListBuilder(repository.getMovies().mapByPage { orderBy(it) }, config).build()
    }

    private fun orderBy(movies: List<Movie>): List<Movie>
    {
        return when(order)
        {
            Order.TITLE -> movies.sortedBy { movie -> movie.title }
            Order.DATE -> movies.sortedByDescending { movie -> movie.releaseDate }
        }
    }

    fun moviesObserver(): LiveData<PagedList<Movie>> = movies

    fun moviesOrderBy(order: Order)
    {
        this.order = order
        movies.value?.dataSource?.invalidate()
    }
}