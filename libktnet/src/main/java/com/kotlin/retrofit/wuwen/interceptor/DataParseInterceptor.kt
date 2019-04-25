package com.kotlin.retrofit.wuwen.http

import io.reactivex.functions.Function

/**
 * 拦截业务异常，并完成Data数据解析
 *
 */
class DataParseInterceptor<T>: Function<HttpResponse<T>, T> {

    override fun apply(result: HttpResponse<T>): T {

        if(HttpResponse.CODE_SUCCESS != result.code && HttpResponse.CODE_GET_TOKEN_SUCCESS != result.code) {
            throw ExceptionWrapper( HttpCode.STATUS_OK, ServerException(result.code.toString(), result.message))
        }

        if(null !=  result.data) {
            return  result.data!!
        }
        if(null != result.dataList) {
            return result.dataList!!
        }

        if(null == result.data) {
            throw DataEmptyException()
        }
        if (null == result.dataList) {
            throw DataEmptyException()
        }

        return result.data!!
    }
}