package com.dao.mymovies.features.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dao.mymovies.Extras
import com.dao.mymovies.R
import com.dao.mymovies.data.MovieRepository
import com.dao.mymovies.model.Movie
import com.dao.mymovies.util.Event
import com.dao.mymovies.util.Logger
import com.dao.mymovies.util.ToastContent
import com.dao.mymovies.util.extensions.contrastColor
import com.dao.mymovies.util.withSchedulers
import io.reactivex.CompletableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created in 07/08/19 08:53.
 *
 * @author Diogo Oliveira.
 */
class MovieDetailViewModel @Inject constructor(
        private val repository: MovieRepository,
        private val composite: CompositeDisposable): ViewModel()
{
    private val showToastObservable = MutableLiveData<ToastContent>()
    val showToast: LiveData<ToastContent> = showToastObservable

    private val scrimColorObservable = MutableLiveData<@ColorInt Int>()
    val scrimColorHomeIndicator: LiveData<Int> = scrimColorObservable

    private val changeMovieFavoriteObservable = MutableLiveData<Event<Unit>>()
    val changeMovieFavoriteSuccess: LiveData<Event<Unit>> = changeMovieFavoriteObservable

    private val movieObservable = MutableLiveData<Movie>()
    val movie: MutableLiveData<Movie> = movieObservable


    fun onInstanceState(bundle: Bundle?, savedState: Boolean)
    {
        if(bundle != null)
        {
            movie.value = bundle.getParcelable(Extras.MOVIE)

            if(!savedState)
            {
                isFavorite { favorite -> movie.value?.isFavorite?.set(favorite) }
            }
        }
        else
        {
            toastContent(R.string.movie_detail_not_found)
        }
    }

    fun setColorHomeIndicator(): RequestListener<Bitmap>
    {
        return object : RequestListener<Bitmap>
        {
            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean
            {
                resource?.let { bitmap ->
                    Palette.from(Bitmap.createBitmap(bitmap, 0, 0, 100, 100)).generate { palette ->
                        palette?.let { scrimColorObservable.postValue(it.contrastColor()) }
                    }
                }
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean
            {
                e?.also { Logger.e(it) }
                return false
            }
        }
    }

    fun movieAction()
    {
        isFavorite { favorite -> if(favorite) movieNotFavorite() else movieFavorite() }
    }

    private fun movieFavorite()
    {
        movie.value?.let {movie ->
            val disposable = repository.save(movie)
                    .compose(withSchedulers<CompletableTransformer>())
                    .subscribe({ changeMovieFavorite(true) },
                            { toastContent(R.string.app_internal_error_client) })

            composite.add(disposable)
        }
    }

    private fun movieNotFavorite()
    {
        movie.value?.let { movie ->
            val disposable = repository.delete(movie)
                    .compose(withSchedulers<CompletableTransformer>())
                    .subscribe({ changeMovieFavorite(false) },
                            { toastContent(R.string.app_internal_error_client) })

            composite.add(disposable)
        }
    }

    private fun changeMovieFavorite(favorite: Boolean)
    {
        movie.value?.let { movie ->
            movie.isFavorite.set(favorite)
            changeMovieFavoriteObservable.value = Event(Unit)
        }
    }

    private fun isFavorite(block: (favorite: Boolean) -> Unit)
    {
        movie.value?.let { movie ->
            val consumer = Consumer<Boolean> { block(it) }
            val error = Consumer<Throwable>  { toastContent(R.string.app_internal_error_client) }

            val disposable = repository.isFavorite(movie)
                    .compose(withSchedulers<SingleTransformer<Boolean, Boolean>>())
                    .subscribe(consumer, error)

            composite.add(disposable)
        }
    }

    private fun toastContent(@StringRes text: Int)
    {
        showToastObservable.value = ToastContent(text, Toast.LENGTH_LONG)
    }

    override fun onCleared()
    {
        super.onCleared()
        composite.clear()
    }
}