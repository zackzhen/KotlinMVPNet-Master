package com.kotlin.retrofit.http

import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Error interceptor
 *
 */
class ErrorInterceptor<T>: Function<Throwable, Observable<T>> {
    override fun apply(t: Throwable): Observable<T> {
        return Observable.error(t)
    }
}