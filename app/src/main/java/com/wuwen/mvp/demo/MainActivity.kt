package com.wuwen.mvp.demo

import android.os.Bundle
import com.kotlin.retrofit.wuwen.RetrofitHelper
import com.kotlin.retrofit.wuwen.base.BaseActivity
import com.kotlin.retrofit.wuwen.callbackOn
import com.kotlin.retrofit.wuwen.http.HttpCode
import com.kotlin.retrofit.wuwen.util.GlideManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlideManager.loadImg("https://csdnimg.cn/pubfooter/images/csdn-cxrs.png",imageView)

        val map = HashMap<String, Any>()
        map["userId"] = "123456"
        BaseApplication.getRetrofitService().userToken(RetrofitHelper.getBody(map)).callbackOn(this,
                onStart = {
                }, onComplete = {
                }, success = { it ->
                    promptError(it.toString())
                })


    }

    override fun onError(httpCode: HttpCode, code: String?, error: String?) {
        promptError(error)
    }
}
