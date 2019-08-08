package com.dao.mymovies.features.detail

import com.dao.mymovies.base.IView

/**
 * Created in 03/08/18 16:59.
 *
 * @author Diogo Oliveira.
 */
interface MovieDetailInteractor
{
    interface View : IView
    {
        fun scrimColorHomeIndicatorObserver()

        fun changeMovieFavoriteSuccessObserver()

        fun showToastObserver()
    }
}