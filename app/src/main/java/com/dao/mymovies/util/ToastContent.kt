package com.dao.mymovies.util

import androidx.annotation.StringRes
import com.dao.mymovies.util.annotation.Duration

/**
 * Created in 07/08/19 10:14.
 *
 * @author Diogo Oliveira.
 */
data class ToastContent(
        @StringRes val text: Int,
        @Duration val duration: Int)