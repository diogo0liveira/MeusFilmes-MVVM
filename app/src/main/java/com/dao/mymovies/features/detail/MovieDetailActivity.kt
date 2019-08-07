package com.dao.mymovies.features.detail

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.dao.mymovies.R
import com.dao.mymovies.base.BaseActivity
import com.dao.mymovies.databinding.ActivityMovieDetailBinding
import com.dao.mymovies.util.EventObserver
import com.dao.mymovies.util.annotation.Duration
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import javax.inject.Inject


/**
 * Created in 03/08/18 15:59.
 *
 * @author Diogo Oliveira.
 */
class MovieDetailActivity : BaseActivity(), MovieDetailInteractor.View, View.OnClickListener
{
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, factory).get(MovieDetailViewModel::class.java) }

    private lateinit var helper: ActivityMovieDetailBinding
    private lateinit var drawableHomeIndicator: Drawable

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        helper = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        helper.lifecycleOwner = this

        if(savedInstanceState == null)
        {
            viewModel.onRestoreInstanceState(this.intent.extras, false)
        }

        initializeView()
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

    override fun onClick(view: View)
    {
        when(view.id)
        {
            R.id.button_favorite ->
            {
                viewModel.movieAction()
            }
        }
    }

    override fun initializeView()
    {
        setSupportActionBar(helper.toolbar)
        drawableHomeIndicator = getDrawable(R.drawable.vd_arrow_back_24dp)!!
        DrawableCompat.setTint(drawableHomeIndicator, Color.WHITE)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(drawableHomeIndicator)
        }

        helper.buttonFavorite.setOnClickListener(this)
        scrimColorHomeIndicatorObserver()
    }

    override fun changeMovieFavoriteSuccessObserver()
    {
        viewModel.changeMovieFavoriteSuccess.observe(this, EventObserver {
            setResult(Activity.RESULT_OK)
        })
    }

    override fun scrimColorHomeIndicatorObserver()
    {
        viewModel.scrimColorHomeIndicator.observe(this, Observer { color ->
            if(color == Color.BLACK)
            {
                helper.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.text_primary_light))
                DrawableCompat.setTint(drawableHomeIndicator, Color.BLACK)

                helper.appBar.scrimAnimationDuration = helper.toolbarLayout.scrimAnimationDuration
                helper.appBar.iconHomeIndicator = drawableHomeIndicator
            }
        })
    }

    override fun showToastObserver()
    {
        viewModel.showToast.observe(this, Observer { content ->
            showToast(content.text, content.duration)
        })
    }

    private fun showToast(@StringRes text: Int, @Duration duration: Int)
    {
        when(duration)
        {
            Toast.LENGTH_SHORT -> toast(text)
            Toast.LENGTH_LONG -> longToast(text)
        }
    }
}