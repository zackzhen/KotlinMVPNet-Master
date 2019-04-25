package com.kotlin.retrofit.wuwen

import android.net.ParseException
import com.google.gson.JsonParseException
import com.kotlin.retrofit.wuwen.http.*
import com.kotlin.retrofit.wuwen.util.KLog
import io.reactivex.Observer
import io.reactivex.exceptions.UndeliverableException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * AbsObserver
 *
 */
abstract class AbsObserver<T>(target: Any?): Observer<T> {
    private var mTarget: Any? = target

    override fun onError(ex: Throwable) {
        // 如果异常类型为DataEmptyException，引导调用onDataEmpty回调方法
        if(ex is DataEmptyException) {
            onDataEmpty()
            return
        }

        if (ex is UndeliverableException){
            val message = "网络处理异常"
            if(!onError(HttpCode.STATUS_OK, HttpResponse.CODE_JSON_PARSE_ERR, message)) {
                (mTarget as? OnHttpError)?.onError(
                        HttpCode.STATUS_OK, HttpResponse.CODE_JSON_PARSE_ERR, message)
            }
            return
        }

        // 如果当前网络不可用，忽略所有HTTP错误，引导进入网络异常提示
     /*   if(!BaseApplication.get().isNetworkConnected()) {
            val message = "网络连接失败，请检查当前网络配置"
            val code = HttpCode.NETWORK_NOT_CONNECTED

            if(!onError(code, null, message)) {
                (mTarget as? OnHttpError)?.onError(code, null, message)
            }
            return
        }*/

        if(ex is ExceptionWrapper) {
            val exception = ex.ex
            if(exception is ServerException) {
                if(!onError(HttpCode.STATUS_OK, exception.code, exception.message)) {
                    (mTarget as? OnHttpError)?.onError(
                            HttpCode.STATUS_OK, exception.code, exception.message)

                }
            }
        } else if(ex is JsonParseException
                || ex is JSONException
                || ex is ParseException) {
            val message = "数据解析异常"
            if(!onError(HttpCode.STATUS_OK, HttpResponse.CODE_JSON_PARSE_ERR, message)) {
                (mTarget as? OnHttpError)?.onError(
                        HttpCode.STATUS_OK, HttpResponse.CODE_JSON_PARSE_ERR, message)
            }
        } else if(ex is HttpException) {
            var message: String? = null

            when(ex.code()){
                HttpCode.UNAUTHORIZED.intValue(), HttpCode.FORBIDDEN.intValue() -> {
                    KLog.e("Http code = ${ex.code()}")
                }
                HttpCode.NOT_FOUND.intValue(),
                HttpCode.REQUEST_TIMEOUT.intValue(),
                HttpCode.GATEWAY_TIMEOUT.intValue(),
                HttpCode.INTERNAL_SERVER_ERROR.intValue(),
                HttpCode.BAD_GATEWAY.intValue(),
                HttpCode.SERVICE_UNAVAILABLE.intValue() -> {
                    message = "服务器繁忙，请稍后再试"
                }
            }

            val httpCode = HttpCode.mapIntValue(ex.code())
            if(!onError(httpCode, null, message)) {
                (mTarget as? OnHttpError)?.onError(httpCode, null, message)
            }
        } else if(ex is UnknownHostException || ex is ConnectException) {
            var message: String? = "服务器出现异常，请稍后再试"
            var code: HttpCode = HttpCode.DNS_ERROR

//            if(!BaseApplication.get().isNetworkConnected() || ex is ConnectException) {
//                message = "网络连接失败，请检查当前网络配置"
//                code = HttpCode.NETWORK_NOT_CONNECTED
//            }

            if(!onError(code, null, message)) {
                (mTarget as? OnHttpError)?.onError(code, null, message)
            }
        } else {
            val message: String = "服务器繁忙，请稍后再试"
            val code: HttpCode = HttpCode.UNKNOWN

            if(!onError(code, null, message)) {
                (mTarget as? OnHttpError)?.onError(code, null, message)
            }
        }
        KLog.e("${ex?.message}")
    }

    abstract fun onError(httpCode: HttpCode, businessCode: String?, error: String?): Boolean

    /**
     * 后台返回数据为空，引导进入HTTP正常返回数据分支
     */
    abstract fun onDataEmpty()
}