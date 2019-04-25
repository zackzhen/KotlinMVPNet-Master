package com.kotlin.retrofit.wuwen.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.kotlin.retrofit.wuwen.R



class Dialog(context: Context) : android.app.Dialog(context, R.style.DialogStyle) {
    private val mMessageText: TextView
    private val mNegativeBtn: Button
    private val mPositiveBtn: Button
    private val mMiddleDivideLine: View
    private val mDividerLine: View
    private val mButtonLayout: LinearLayout
    private val mContext = context

    private var onNegativeButtonClicked: ((negativeButton: Button)->Unit)? = null
    private var onPositiveButtonClicked: ((positiveButton: Button)->Unit)? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setContentView(R.layout.sweet_dialog)

        mMessageText = findViewById(R.id.text_message)
        mNegativeBtn = findViewById(R.id.btn_negative)
        mPositiveBtn = findViewById(R.id.btn_positive)
        mDividerLine = findViewById(R.id.divider_line)
        mMiddleDivideLine = findViewById(R.id.middle_divider_line)
        mButtonLayout = findViewById(R.id.layout_button)

        mNegativeBtn.onSingleClick { button->
            onNegativeButtonClicked?.invoke(button as Button)
            dismiss()
        }

        mPositiveBtn.onSingleClick { button->
            onPositiveButtonClicked?.invoke(button as Button)
            dismiss()
        }
    }

    fun message(message: CharSequence) {
        mMessageText.text = message
    }
    fun setMessageRight(){
        mMessageText.gravity = Gravity.LEFT
    }

    fun setBold(){
        mMessageText.setTextColor(Color.parseColor("#333333"))
        mMessageText.typeface = Typeface.DEFAULT_BOLD
    }
    fun setNormal(){
        mMessageText.setTextColor(Color.parseColor("#666666"))
        mMessageText.typeface = Typeface.DEFAULT
    }

    fun message(@StringRes stringRes: Int) {
        message(mContext.getString(stringRes))
    }

    fun negativeButton(visible: Boolean = true ,
                       text: CharSequence = context.getString(R.string.cancel) ,
                       onNegativeButtonClicked: ((negativeButton: Button)->Unit)? = null) {
        this.onNegativeButtonClicked = onNegativeButtonClicked
        mNegativeBtn.text = text
        mNegativeBtn.visibility = if(visible) View.VISIBLE else View.GONE
        updateButtonLayoutVisibility()
    }

    fun positiveButton(visible: Boolean = true ,
                       text: CharSequence = context.getString(R.string.sure) ,
                       onPositiveButtonClicked: ((positiveButton: Button)->Unit)? = null) {
        this.onPositiveButtonClicked = onPositiveButtonClicked
        mPositiveBtn.text = text
        mPositiveBtn.visibility = if(visible) View.VISIBLE else View.GONE
        updateButtonLayoutVisibility()
    }

    private fun updateButtonLayoutVisibility() {
        mButtonLayout.visibility = if(mNegativeBtn.visibility == View.GONE &&
                mPositiveBtn.visibility == View.GONE) View.GONE else View.VISIBLE
        mDividerLine.visibility = if(mNegativeBtn.visibility == View.GONE &&
                mPositiveBtn.visibility == View.GONE) View.GONE else View.VISIBLE
        mMiddleDivideLine.visibility = if(mNegativeBtn.visibility == View.VISIBLE
                && mPositiveBtn.visibility == View.VISIBLE) View.VISIBLE else View.GONE
    }
}

fun Context.dialog(message: CharSequence ,
                   init: (Dialog.()->Unit)? = null): Dialog {
    val dialog = Dialog(this)
    dialog.message(message)
    if(null != init) {
        dialog.init()
    }
    return dialog
}

fun Fragment.dialog(message: CharSequence ,
                    init: (Dialog.()->Unit)? = null): Dialog {
    return activity!!.dialog(message , init)
}