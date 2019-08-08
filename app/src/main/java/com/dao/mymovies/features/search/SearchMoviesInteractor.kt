package com.dao.mymovies.features.search

import androidx.annotation.StringRes
import com.dao.mymovies.base.IView
import com.dao.mymovies.base.OnCollectionChangedListener
import com.dao.mymovies.features.adapter.MyMoviesAdapter
import com.dao.mymovies.util.annotation.Duration

/**
 * Created in 06/08/18 10:42.
 *
 * @author Diogo Oliveira.
 */
interface SearchMoviesInteractor
{
    interface View : IView, OnCollectionChangedListener, MyMoviesAdapter.MovieViewOnClickListener
    {
        fun networkStateObserver()

        fun showToast(@StringRes text: Int, @Duration duration: Int)

        fun notifyError(@StringRes text: Int, block: () -> Unit)

        fun executeRequireNetwork(block: () -> Unit)
    }
}