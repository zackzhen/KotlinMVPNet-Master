package com.kotlin.retrofit.wuwen.util

import android.util.Log
import com.kotlin.retrofit.wuwen.BuildConfig


/**
 * Log日志简单实现
 *
 *
 */
class KLog {
    companion object {
        private const val TAG = "接口日志"

        fun e(tag: String , message: String) {
            if(BuildConfig.DEBUG) {
                Log.e(tag , message)
            }
        }

        fun d(tag: String , message: String) {
            if(BuildConfig.DEBUG) {
                Log.e(tag , message)
            }
        }

        fun e(message: String) {
            e(TAG, message)
        }

        fun d(message: String) {
            d(TAG, message)
        }
    }
}