package com.kotlin.retrofit.wuwen.http

/**
 * 接口异常封装类
 *
 */
data class ExceptionWrapper(var httpCode: HttpCode, var ex: ServerException): Exception() {

}