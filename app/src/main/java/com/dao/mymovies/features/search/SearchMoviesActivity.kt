package com.dao.mymovies.features.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import com.dao.mymovies.Extras.MOVIE
import com.dao.mymovies.R
import com.dao.mymovies.REQUEST.REQUEST_DETAIL_MOVIE
import com.dao.mymovies.base.BaseActivity
import com.dao.mymovies.databinding.ActivitySearchMoviesBinding
import com.dao.mymovies.databinding.ViewEmptySearchMoviesBinding
import com.dao.mymovies.features.adapter.MyMoviesAdapter
import com.dao.mymovies.features.detail.MovieDetailActivity
import com.dao.mymovies.model.Movie
import com.dao.mymovies.network.NetworkState
import com.dao.mymovies.network.State
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * Created in 03/08/18 16:00.
 *
 * @author Diogo Oliveira.
 */
class SearchMoviesActivity : BaseActivity(), SearchMoviesInteractor.View
{
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, factory).get(SearchMoviesViewModel::class.java) }

    private lateinit var helperEmpty: ViewEmptySearchMoviesBinding
    private lateinit var helper: ActivitySearchMoviesBinding
    private lateinit var searchView: SearchView

    private val adapter: MyMoviesAdapter by lazy { MyMoviesAdapter(this, this) }

    override fun onCreate(@Nullable savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        helper = DataBindingUtil.setContentView(this, R.layout.activity_search_movies)
        initializeView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.option_search_movies, menu)
        val item = menu.findItem(R.id.action_search)
        searchView = item.actionView as SearchView

        searchView.setIconifiedByDefault(true)
        searchView.isIconified = false

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.onActionViewExpanded()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                query?.let {
                    removeSnackNotify()
                    viewModel.searchMovies(it)
                    searchView.clearFocus()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                newText?.let {
                    removeSnackNotify()
                    adapter.clean()
                }
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                true
            }
            else ->
            {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?)
    {
        when(requestCode)
        {
            REQUEST_DETAIL_MOVIE ->
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    setResult(Activity.RESULT_OK)
                }
            }
            else ->
            {
                super.onActivityResult(requestCode, resultCode, intent)
            }
        }
    }

    override fun initializeView()
    {
        helper.viewState.viewStub?.inflate()?.let { view ->
            DataBindingUtil.getBinding<ViewEmptySearchMoviesBinding>(view)?.let { binding ->
                helperEmpty = binding
                helperEmpty.visible = true
            }
        }

        with(helper.searchList) {
            val divider = DividerItemDecoration(this@SearchMoviesActivity, DividerItemDecoration.VERTICAL)
            addItemDecoration(divider)
            setHasFixedSize(true)
        }

        adapter.setOnCollectionChangedListener(this)
        helper.searchList.adapter = adapter

        networkStateObserver()
        viewModel.searchObserver().observe(this, Observer { adapter.submitList(it) })
    }

    override fun onMovieViewOnClick(movie: Movie)
    {
        startActivityForResult<MovieDetailActivity>(REQUEST_DETAIL_MOVIE, Pair(MOVIE, movie))
    }

    override fun showToast(text: Int, duration: Int)
    {
        when(duration)
        {
            Toast.LENGTH_SHORT -> toast(text)
            Toast.LENGTH_LONG -> longToast(text)
        }
    }

    override fun onCollectionChanged(isEmpty: Boolean)
    {
        helperEmpty.visible = isEmpty
    }

    override fun executeRequireNetwork(block: () -> Unit)
    {
        if(isNetworkConnected())
        {
            removeSnackNotify()
            block()
        }
        else
        {
            notifyError(R.string.app_internal_no_connection) { executeRequireNetwork(block) }
        }
    }

    override fun networkStateObserver()
    {
        viewModel.networkStateObserver().observe(this, Observer { state ->
            when(state)
            {
                is NetworkState.Running ->
                {
                    helperEmpty.showProgressBar = (adapter.itemCount == 0)
                }
                is NetworkState.Success ->
                {
                    helperEmpty.showProgressBar = false
                }
                is NetworkState.Error ->
                {
                    when(state.state)
                    {
                        State.FAILED ->
                        {
                            executeRequireNetwork { state.retry.invoke() }
                        }
                        State.UNAVAILABLE ->
                        {
                            state.message?.let { message ->
                                notifyError(message) { executeRequireNetwork(state.retry) }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun notifyError(@StringRes text: Int, block: () -> Unit)
    {
        helperEmpty.showProgressBar = false
        helperEmpty.visible = (adapter.itemCount == 0)
        showSnackNotify(helper.anchor, text) { block() }
    }
}
