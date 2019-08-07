package com.dao.mymovies.features.detail

import android.os.Bundle
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dao.mymovies.Extras
import com.dao.mymovies.MovieFactory
import com.dao.mymovies.R
import com.dao.mymovies.data.local.FakeMoviesLocalDataSource
import com.dao.mymovies.data.remote.FakeMoviesRemoteDataSource
import com.dao.mymovies.data.repository.FakeMovieRepository
import com.dao.mymovies.model.Movie
import com.dao.mymovies.util.ToastContent
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Created in 07/08/19 09:04.
 *
 * @author Diogo Oliveira.
 */
class MovieDetailViewModelTest
{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var composite: CompositeDisposable

    private lateinit var viewModel: MovieDetailViewModel
    private lateinit var repository: FakeMovieRepository

    init
    {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Before
    fun setUp()
    {
        MockitoAnnotations.initMocks(this)

        repository = FakeMovieRepository(FakeMoviesLocalDataSource(), FakeMoviesRemoteDataSource())
        viewModel = MovieDetailViewModel(repository, composite)
    }

    @Test
    fun `restore instance state`()
    {
        val movie = MovieFactory.build(1)
        val bundle = mock(Bundle::class.java)

        doReturn(movie).`when`(bundle).getParcelable<Movie>(Extras.MOVIE)
        viewModel.onRestoreInstanceState(bundle, false)
        assertThat(viewModel.movie.value, `is`(movie))
    }

    @Test
    fun `restore instance state empty`()
    {
        val bundle = null
        viewModel.onRestoreInstanceState(bundle, false)
        assertThat(viewModel.showToast.value, `is`(ToastContent(R.string.movie_detail_not_found, Toast.LENGTH_LONG)))
    }

    @Test
    fun `movie action is favorite`()
    {
        val movie = MovieFactory.build(1)
        movie.isFavorite.set(false)

        val bundle = mock(Bundle::class.java)
        doReturn(movie).`when`(bundle).getParcelable<Movie>(Extras.MOVIE)

        viewModel.onRestoreInstanceState(bundle, false)
        viewModel.movieAction()

        repository.isFavorite(movie)
                .test()
                .assertValue(true)
    }

    @Test
    fun `movie action not is favorite`()
    {
        val movie = MovieFactory.build(1)
        movie.isFavorite.set(true)

        val bundle = mock(Bundle::class.java)
        doReturn(movie).`when`(bundle).getParcelable<Movie>(Extras.MOVIE)
        repository.save(movie)

        viewModel.onRestoreInstanceState(bundle, false)
        viewModel.movieAction()

        repository.isFavorite(movie)
                .test()
                .assertValue(false)
    }
}