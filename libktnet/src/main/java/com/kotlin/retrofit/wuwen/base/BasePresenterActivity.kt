package com.kotlin.retrofit.wuwen.base

import android.os.Bundle
import com.kotlin.retrofit.wuwen.view.BasePresenter
import com.kotlin.retrofit.wuwen.view.BaseView
import com.kotlin.retrofit.wuwen.view.bindLoadingDialog
import java.lang.reflect.ParameterizedType

abstract class BasePresenterActivity<V: BaseView, out P: BasePresenter<V>> : BaseActivity() {
    protected val mPresenter: P? by lazy {

        try {
            val clazz: Class<P>? = (javaClass.genericSuperclass as ParameterizedType)
                    .actualTypeArguments[1]
                    as? Class<P>
            clazz?.newInstance()
        } catch(e: Exception) {
            null
        }
    }
    protected val mLoadingDialog by bindLoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter?.view = presenterView()
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