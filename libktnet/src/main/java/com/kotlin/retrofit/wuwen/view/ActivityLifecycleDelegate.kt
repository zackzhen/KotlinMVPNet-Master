package com.kotlin.retrofit.wuwen.view

import android.content.Intent
import android.os.Bundle

/**
 * Activity生命周期代理
 *
 */
abstract class ActivityLifecycleDelegate {
    /**
     * 在[android.app.Activity.onCreate]生命周期方法执行的时候调用
     */
    open fun onCreate(savedInstanceState: Bundle?) {}

    /**
     * 在[android.app.Activity.onStart]生命周期方法执行的时候调用
     */
    open fun onStart() {}

    /**
     * 在[android.app.Activity.onResume]生命周期方法执行的时候调用
     */
    open fun onResume() {}

    /**
     * 在[android.app.Activity.onPause]生命周期方法执行的时候调用
     */
    open fun onPause() {}

    /**
     * 在[android.app.Activity.onStop]生命周期方法执行的时候调用
     */
    open fun onStop() {}

    /**
     * 在[android.app.Activity.onDestroy]生命周期方法执行的时候调用
     */
    open fun onDestroy() {}

    /**
     * 在[android.app.Activity.onActivityResult]生命周期方法执行的时候调用
     */
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
}