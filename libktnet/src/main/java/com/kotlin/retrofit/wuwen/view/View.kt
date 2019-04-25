package com.kotlin.retrofit.wuwen.view

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kotlin.retrofit.wuwen.util.openDial
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 解决连续单击问题
 */
fun View.onSingleClick(action: (view: View)->Unit) {
//    RxView.clicks(this).throttleFirst(500, TimeUnit.MILLISECONDS)
//            .subscribe {
//                action.invoke(this)
//            }
}



/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun Fragment.bindDialog(init: (Dialog.()->Unit)? = null):
        DialogLazyBinder<Fragment> = buildDialog(init)

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun Context.bindDialog(init: (Dialog.()->Unit)? = null):
        DialogLazyBinder<Context> = buildDialog(init)

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun android.app.Dialog.bindDialog(init: (Dialog.()->Unit)? = null): DialogLazyBinder<Dialog>
        = buildDialog(init)

fun <T> buildDialog(init: (Dialog.()->Unit)? = null) = DialogLazyBinder<T>(init)

class DialogLazyBinder<T>(private var initialize: (Dialog.() -> Unit)? = null):
        ReadOnlyProperty<T, Dialog> {
    private var mDialog: Dialog? = null

    override fun getValue(thisRef: T, property: KProperty<*>): Dialog {
        if(null == mDialog) {
            if(thisRef is Context) {
                mDialog = Dialog(thisRef)
            }

            if(thisRef is Fragment) {
                mDialog = Dialog(thisRef.activity!!)
            }

            if(thisRef is android.app.Dialog) {
                mDialog = Dialog(thisRef.context)
            }

            mDialog!!.setCanceledOnTouchOutside(false)
            initialize?.invoke(mDialog!!)
        }
        return mDialog!!
    }
}

/**
 * 使用代理方式创建Loading对话框
 *
 * @param init 初始化函数
 */
fun Fragment.bindLoadingDialog(init: (LoadingDialog.()->Unit)? = null):
        LoadingDialogLazyBinder<Fragment> = buildLoadingDialog(init)

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun Context.bindLoadingDialog(init: (LoadingDialog.()->Unit)? = null):
        LoadingDialogLazyBinder<Context> = buildLoadingDialog(init)

/**
 * 使用代理方式创建对话框
 *
 * @param init 初始化函数
 */
fun android.app.Dialog.bindLoadingDialog(init: (LoadingDialog.()->Unit)? = null):
        LoadingDialogLazyBinder<LoadingDialog> = buildLoadingDialog(init)

fun <T> buildLoadingDialog(init: (LoadingDialog.()->Unit)? = null) =
        LoadingDialogLazyBinder<T>(init)


class LoadingDialogLazyBinder<T>(private var initialize: (LoadingDialog.()->Unit)? = null)
    : ReadOnlyProperty<T, LoadingDialog> {
    private var mLoadingDialog: LoadingDialog? = null

    override fun getValue(thisRef: T, property: KProperty<*>): LoadingDialog {
        if(null == mLoadingDialog) {
            if(thisRef is Context) {
                mLoadingDialog = LoadingDialog(thisRef)
            }

            if(thisRef is Fragment) {
                mLoadingDialog = LoadingDialog(thisRef.activity!!)
            }

            if(thisRef is android.app.Dialog) {
                mLoadingDialog = LoadingDialog(thisRef.context)
            }

            initialize?.invoke(mLoadingDialog!!)
        }

        return mLoadingDialog!!
    }
}

/**
 * 调起系统拨号界面
 *
 * @param mobile 手机号
 */
fun Fragment.openDial(mobile: String) {
    activity?.openDial(mobile)
}

/**
 * 调起系统拨号界面
 *
 * @param mobile 手机号
 */
fun android.app.Fragment.openDial(mobile: String) {
    activity.openDial(mobile)
}

/**
 * 隐藏软键盘
 *
 * @param context Context
 * @param view 触发聚焦的View对象
 */
fun hideKeyboard(context: Context, view: View?) {
    if(null != view) {
        val inputMethodManager: InputMethodManager = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * 显示软键盘
 *
 * @param context Context
 * @param view 触发聚焦的View对象
 */
fun showKeyboard(context: Context, view: View?) {
    if(null != view) {
        val inputMethodManager: InputMethodManager = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
}
