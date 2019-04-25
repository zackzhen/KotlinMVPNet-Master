package com.wuwen.mvp.demo

import android.app.Application
import com.kotlin.retrofit.wuwen.RetrofitHelper

class BaseApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        retrofit = RetrofitHelper.getDefault().create(ApiService::class.java)

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