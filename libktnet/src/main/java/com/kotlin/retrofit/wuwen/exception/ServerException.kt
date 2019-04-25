package com.kotlin.retrofit.wuwen.http

/**
 * Server exception
 *
 */
data class ServerException(var code: String?, var error: String?): Exception(error) {

}