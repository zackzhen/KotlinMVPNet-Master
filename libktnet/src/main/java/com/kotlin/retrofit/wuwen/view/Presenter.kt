package com.kotlin.retrofit.wuwen.view

/**
 * Presenter基础接口
 */
interface Presenter {
    /**
     * 对应[android.app.Activity.onStart]和[android.app.Fragment.onStart]生命周期回调
     */
    fun onStart()
    /**
     * 对应[android.app.Activity.onStop]和[android.app.Fragment.onStop]生命周期回调
     */
    fun onStop()
    /**
     * 对应[android.app.Activity.onDestroy]和[android.app.Fragment.onDestroy]生命周期回调
     */
    fun onDestroy()
}