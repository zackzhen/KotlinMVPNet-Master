package com.kotlin.retrofit.wuwen.view

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.kotlin.retrofit.wuwen.R
import com.kotlin.retrofit.wuwen.base.BaseActivity
import com.kotlin.retrofit.wuwen.base.BaseFragment
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.http.HttpResponse

import java.util.*

/**
 * 该类用于统一错误处理网络错误
 *
 */
class ErrorMachine {
    private var mDialog: Dialog? = null

    companion object {
        private val mActivityMap = HashMap<Activity, ErrorMachine>()

        fun create(activity: BaseActivity): ErrorMachine {
            var machine = mActivityMap[activity]

            if(null == machine) {
                machine = ErrorMachine()
            }
            mActivityMap.put(activity, machine)

            return machine
        }

        fun create(fragment: BaseFragment): ErrorMachine {
            var machine = mActivityMap[fragment.activity!!]

            if(null == machine) {
                machine = ErrorMachine()
            }
            mActivityMap.put(fragment.activity!!, machine)

            return machine
        }
    }

    private fun accountException(activity: Activity?, error: String?) {
        if(null == activity) return

        if(null == mDialog) {
            mDialog = Dialog(activity)
        }
        mDialog!!.setCanceledOnTouchOutside(false)
        mDialog!!.setCancelable(false)
        mDialog!!.message(error ?: "")
        mDialog!!.negativeButton(visible = false)
        mDialog!!.positiveButton(visible = true, text = activity.getString(R.string.go_to_login)) {
//            UserManager.logout()
//            EventBus.getDefault().post(Event(Event.TYPE_LOGOUT_SUCCESS))
//            activity.startActivity<LoginActivity>()
        }
        if(!mDialog!!.isShowing) {
            mDialog!!.show()
        }
    }

    private fun networkException(activity: Activity?, error: String?) {
        if(null == activity) return

        if(null == mDialog) {
            mDialog = Dialog(activity)
        }

        mDialog!!.message(error ?: "")
        mDialog!!.negativeButton(visible = true,
                                 text = activity.getString(R.string.cancel)) {
            mDialog!!.dismiss()
        }
        mDialog!!.positiveButton(visible = true,
                                 text = activity.getString(R.string.sure)) {
            activity.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }

        if(!mDialog!!.isShowing) {
            mDialog!!.show()
        }
    }

    fun onError(activity: Activity?, code: HttpCode, bizCode: String?, error: String?) {
        // 异地登录，会话已过期
        if(HttpResponse.CODE_ACCOUNT_EXCEPTION == bizCode /*|| HttpResponse.CODE_SESSION_EXPIRED == bizCode*/) {
            accountException(activity, error)
            return
        }

        // 网络异常
        if(code == HttpCode.NETWORK_NOT_CONNECTED) {
            networkException(activity, error)
            return
        }

        if(!TextUtils.isEmpty(error)) {
            (activity as? BaseActivity)?.promptError(error)
        }
    }

    fun onDestroy(activity: Activity) {
        mActivityMap.remove(activity)
        mDialog?.dismiss()
    }
}