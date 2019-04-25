package com.wuwen.mvp.demo

import android.app.Application
import com.kotlin.retrofit.wuwen.RetrofitHelper
import com.kotlin.retrofit.wuwen.util.CrashUtil

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        retrofit = RetrofitHelper.getDefault().create(ApiService::class.java)

        CrashUtil.getInstance().init(this)
    }

    companion object {
        private var retrofit: ApiService? =null
        private var instance: BaseApplication? = null
        fun get(): BaseApplication {

            return instance!!
        }

        fun getRetrofitService(): ApiService {
            return retrofit!!
        }
    }


}