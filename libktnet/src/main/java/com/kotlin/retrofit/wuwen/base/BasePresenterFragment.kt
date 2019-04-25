package com.kotlin.retrofit.wuwen.base

import android.os.Bundle
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.view.BasePresenter
import com.kotlin.retrofit.wuwen.view.BaseView
import com.kotlin.retrofit.wuwen.view.bindLoadingDialog
import java.lang.reflect.ParameterizedType

abstract class BasePresenterFragment<V: BaseView, out P: BasePresenter<V>>: BaseFragment() {
    protected val mPresenter: P? by lazy {
        try {
            val clazz: Class<P>? = (javaClass.genericSuperclass as ParameterizedType)
                    .actualTypeArguments[1]
                    as? Class<P>
            clazz?.newInstance()
        } catch(e: Exception) {
            createPresenter()
        }
    }
    protected val mLoadingDialog by bindLoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter?.view = presenterView()
    }

    /**
     * 正常情况下不需要实现该方法，仅在自动创建Presenter失败的情况下重新该方法
     */
    open fun createPresenter(): P {
        throw RuntimeException("自动创建Presenter失败，请实现该方法手动创建Presenter")
    }

    override fun onError(httpCode: HttpCode, code: String?, error: String?) {
        onBeforeError(httpCode, code, error)
        super.onError(httpCode, code, error)
    }

    /**
     * 统一错误处理之前的操作
     */
    open fun onBeforeError(httpCode: HttpCode, code: String?, error: String?) {
        mLoadingDialog.dismiss()
    }

    abstract fun presenterView(): V?

    override fun onStart() {
        super.onStart()
        mPresenter?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mPresenter?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
        mLoadingDialog.dismiss()
    }

}