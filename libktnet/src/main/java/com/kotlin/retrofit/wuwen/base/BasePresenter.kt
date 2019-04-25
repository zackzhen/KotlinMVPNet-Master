package com.kotlin.retrofit.wuwen.view


abstract class BasePresenter<V: BaseView>: Presenter {
    var view: V? = null
}