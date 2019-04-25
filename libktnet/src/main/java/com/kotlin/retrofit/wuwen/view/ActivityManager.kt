package com.kotlin.retrofit.wuwen.view

import com.kotlin.retrofit.wuwen.base.BaseActivity


/**
 * Activity manager

 */
abstract class ActivityManager {

    abstract fun push(activity: BaseActivity)
    abstract fun pop(cls: Class<out BaseActivity>)
    abstract fun pop()
    abstract fun pop(vararg classes: Class<out BaseActivity>)
    abstract fun popToActivity(cls: Class<out BaseActivity>)
    abstract fun remove(activity: BaseActivity)
    abstract fun removeAll()

    companion object {
        private var instance: ActivityManager? = null

        fun newInstance(): ActivityManager {
            if (null == instance) {
                instance = ActivityManagerImpl()
            }
            return instance!!
        }
    }
}
