package com.dao.mymovies.features.list

import com.dao.mymovies.base.OnCollectionChangedListener
import com.dao.mymovies.base.mvp.IView
import com.dao.mymovies.features.adapter.MyMoviesAdapter

/**
 * Created in 03/08/18 12:02.
 *
 * @author Diogo Oliveira.
 */
interface MyMoviesInteractor
{
    interface View : IView, OnCollectionChangedListener, MyMoviesAdapter.MovieViewOnClickListener
    {
        fun startSearchMoviesActivity()
    }
}