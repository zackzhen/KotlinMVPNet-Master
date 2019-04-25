package com.kotlin.retrofit.wuwen

import com.kotlin.retrofit.http.ErrorInterceptor
import com.kotlin.retrofit.wuwen.base.BaseActivity
import com.kotlin.retrofit.wuwen.http.DataParseInterceptor
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.http.HttpResponse
import com.kotlin.retrofit.wuwen.view.BaseView
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.RxActivity
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.components.support.RxFragment
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 订阅观察者，并添加统一错误处理逻辑
 *
 * @param observer 观察者
 */
fun <T> Observable<HttpResponse<T>>.subscribeWithErrorHandling(observer: AbsObserver<T>) {
    errorHandling().subscribe(observer)
}


/**
 * 统一错误处理
 *
 * @return [Observable]
 */
fun <T> Observable<HttpResponse<T>>.errorHandling(): Observable<T> {
    return onErrorResumeNext(ErrorInterceptor<HttpResponse<T>>())
            .map(DataParseInterceptor<T>())
}

/**
 * 使用callBackOn主要做了下面2件事情：
 * 1）自动完成接口通用错误处理，例如：
 *    a）接口返回错误信息，统一弹窗提示
 *    b）接口返回要求会话过期等信息，提示重新登录
 * 注意：要实现通用错误处理，必须指定[targetView]参数，并且[targetView]对象所在的类必须实现[OnHttpError]接口
 * 这里通常的实现是：让[com.aony.qibao.ui.BaseActivity]或[com.aony.qibao.ui.BaseFragment]实现[OnHttpError]
 * 接口，并且在回调里面做统一错误处理
 *
 * 2）在[com.aony.qibao.ui.BaseActivity.onDestroy]或[com.aony.qibao.ui.BaseFragment.onDestroyView]方法中
 *    自动完成RxJava的反订阅，
 *
 * --------------------------------------------------------------------------
 * 如果希望自己处理某个错误，请传入[fail]参数，并将返回值设置为true，这个时候系统将使用你的[fail]回调进行统一错
 * 误处理，而忽略统一错误处理逻辑
 * --------------------------------------------------------------------------
 *
 * @param targetView  统一错误将在该对象上调用onError方法（前提：该对象必须实现onHttpError接口）
 * @param fail    请求异常回调，return true 表示自己处理异常，不需要统一处理
 * @param success 请求成功后回调，参数为已经解析准备好的数据对象
 */
fun <T> Observable<HttpResponse<T>>.callbackOn(targetView: BaseView?,
                                               onStart: (() -> Unit)? = null,
                                               onComplete: (() -> Unit)? = null,
                                               fail: ((httpCode: HttpCode,
                                                                                businessCode: String?,
                                                                                error: String?) -> Boolean)? = null,
                                               success: (value: T?) -> Unit) {

    var observable = subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                onStart?.invoke()
            }

    if (targetView is RxAppCompatActivity) {
        observable = observable.autoUnSubscribe(targetView)
    } else if(targetView is RxFragment) {
        observable = observable.autoUnSubscribe(targetView)
    }

    observable.subscribeWithErrorHandling(object : AbsObserver<T>(targetView) {
        override fun onSubscribe(d: Disposable) {
        }

        override fun onComplete() {
        }

        override fun onNext(value: T) {
            success.invoke(value)
            onComplete?.invoke()
        }

        override fun onDataEmpty() {
            success.invoke(null)
            onComplete?.invoke()
        }

        override fun onError(httpCode: HttpCode,
                             businessCode: String?,
                             error: String?): Boolean {
            targetView?.onRequestCompleted()
            return fail?.invoke(httpCode, businessCode, error) ?: false
        }
    })
}


fun <T> Observable<HttpResponse<T>>.callbackOn(targetView: BaseActivity?,
                                               onStart: (() -> Unit)? = null,
                                               onComplete: (() -> Unit)? = null,
                                               fail: ((httpCode: HttpCode,
                                                                                businessCode: String?,
                                                                                error: String?) -> Boolean)? = null,
                                               success: (value: T?) -> Unit) {

    var observable = subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                onStart?.invoke()
            }


    observable.subscribeWithErrorHandling(object : AbsObserver<T>(targetView) {
        override fun onSubscribe(d: Disposable) {
        }

        override fun onComplete() {
        }

        override fun onNext(value: T) {
            success.invoke(value)
            onComplete?.invoke()
        }

        override fun onDataEmpty() {
            success.invoke(null)
            onComplete?.invoke()
        }

        override fun onError(httpCode: HttpCode,
                             businessCode: String?,
                             error: String?): Boolean {
            targetView?.onRequestCompleted()
            return fail?.invoke(httpCode, businessCode, error) ?: false
        }
    })
}

/**
 * 在[RxActivity]的某个生命周期方法执行的时候自动完成反订阅，默认是onDestroy方法
 *
 * @param activity 目标Activity
 * @param activityEvent Activity对应生命周期常量
 *
 * @return [Observable]
 */
fun <T> Observable<T>.autoUnSubscribe(activity: RxAppCompatActivity?,
                                      activityEvent: ActivityEvent = ActivityEvent.DESTROY):
        Observable<T>? {
    return if (null != activity) bindUntilEvent(activity, activityEvent) else null
}

/**
 * 在[RxFragment]的某个生命周期方法执行的时候自动完成反订阅，默认是onDestroy方法
 *
 * @param fragment 目标Fragment
 * @param fragmentEvent Fragment对应生命周期常量
 *
 * @return [Observable]
 */
fun <T> Observable<T>.autoUnSubscribe(fragment: RxFragment?,
                                      fragmentEvent: FragmentEvent = FragmentEvent.DESTROY_VIEW):
        Observable<T>? {
    return if (null != fragment) bindUntilEvent(fragment, fragmentEvent) else null
}