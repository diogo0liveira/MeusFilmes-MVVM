package com.dao.mymovies.network

import androidx.annotation.StringRes

/**
 * Created in 02/04/19 12:06.
 *
 * @author Diogo Oliveira.
 */
enum class State
{
    FAILED, UNAVAILABLE
}

sealed class NetworkState
{
    object Running : NetworkState()

    object Success : NetworkState()

    data class Error(
            val state: State = State.FAILED,
            @StringRes val message: Int? = null,
            val cause: Throwable? = null,
            val retry: (() -> Unit)): NetworkState()
}