package com.dao.mymovies.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created in 06/08/19 16:29.
 *
 * @author Diogo Oliveira.
 */
class ViewModelFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>,
        @JvmSuppressWildcards Provider<ViewModel>>) : ViewModelProvider.Factory
{
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T
//    {
//        try
//        {
//            val found = creators.entries.find { modelClass.isAssignableFrom(it.key) }
//            val creator = found?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
//            @Suppress("UNCHECKED_CAST") return creator.get() as T
//        }
//        catch(e: Exception)
//        {
//            throw RuntimeException(e)
//        }
//    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        val creator = creators[modelClass]
                ?: creators.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
                ?: throw IllegalArgumentException("unknown model class $modelClass")

        return try
        {
            creator.get() as T
        }
        catch(e: Exception)
        {
            throw RuntimeException(e)
        }
    }
}