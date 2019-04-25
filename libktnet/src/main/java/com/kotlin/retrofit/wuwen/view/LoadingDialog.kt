package com.kotlin.retrofit.wuwen.view

import android.app.Dialog
import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.kotlin.retrofit.wuwen.R


class LoadingDialog(context: Context) : Dialog(context , R.style.DialogStyle) {
    private val messageText: TextView

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setContentView(R.layout.loading_dialog)
        messageText = findViewById(R.id.text_message)
    }

    fun message(message: CharSequence) {
        messageText.text = message
        messageText.visibility = if(!TextUtils.isEmpty(message)) View.VISIBLE else View.GONE
    }

    fun message(@StringRes resId: Int) {
        message(context.getString(resId))
    }
}

fun Context.loadingDialog(message: CharSequence? = null ,
                          init: (LoadingDialog.()->Unit)? = null) : LoadingDialog {
    val dialog = LoadingDialog(this)
    if(null != init) {
        dialog.init()
    }
    return dialog
}

fun Fragment.loadingDialog(message: CharSequence? = null,
                           init: (LoadingDialog.()->Unit)? = null) : LoadingDialog {
    return activity!!.loadingDialog(message , init)
}