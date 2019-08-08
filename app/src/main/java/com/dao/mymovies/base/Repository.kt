package com.dao.mymovies.base

/**
 * Created in 26/03/19 20:49.
 *
 * @author Diogo Oliveira.
 */
abstract class Repository<L, R>(protected val local: L, protected val remote: R)
