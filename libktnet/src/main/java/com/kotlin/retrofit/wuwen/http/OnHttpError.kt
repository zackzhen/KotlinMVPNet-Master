package com.kotlin.retrofit.wuwen.http

/**
 * Http错误统一接口
 *
 */
interface OnHttpError {
    fun onError(httpCode: HttpCode, code: String?, error: String?)
//    fun onTokenFail(httpCode: HttpCode, code: String?, error: String?)
}