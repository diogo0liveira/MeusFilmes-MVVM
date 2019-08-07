package com.dao.mymovies.features.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dao.mymovies.data.MovieRepository
import com.dao.mymovies.features.search.paging.SearchDataSourceFactory
import com.dao.mymovies.model.Movie
import com.dao.mymovies.network.NetworkState
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created in 07/08/19 08:45.
 *
 * @author Diogo Oliveira.
 */
class SearchMoviesViewModel @Inject constructor(
        private val repository: MovieRepository,
        private val composite: CompositeDisposable) : ViewModel()
{
    private val query = MutableLiveData<String>()
    private val search = map(query) { searchDataSourceFactory(it) }
    private val searchObserver: LiveData<PagedList<Movie>> = switchMap(search) { it }
    private val networkStateObserver = MediatorLiveData<NetworkState>()

    fun searchObserver(): LiveData<PagedList<Movie>>
    {
        return searchObserver
    }

    fun networkStateObserver(): LiveData<NetworkState>
    {
        return networkStateObserver
    }

    fun searchMovies(query: String)
    {
        this.query.value = query
    }

    private fun searchDataSourceFactory(query: String): LiveData<PagedList<Movie>>
    {
        val factory = SearchDataSourceFactory(query, composite, repository)
        val config = PagedList.Config.Builder().setPageSize(30).build()
        val paged = LivePagedListBuilder(factory, config).build()

        networkStateObserver.addSource(switchMap(factory.source) { it.networkState }) {
            networkStateObserver.postValue(it)
        }

        return paged
    }

    override fun onCleared()
    {
        super.onCleared()
        composite.clear()
    }
}