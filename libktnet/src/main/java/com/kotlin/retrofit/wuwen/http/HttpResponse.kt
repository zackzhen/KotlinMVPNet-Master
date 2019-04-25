package com.kotlin.retrofit.wuwen.http

/**
 * Http请求回包格式
 **/
class HttpResponse<T> {
    var message: String? = null
    var code: String? = null
    var data: T? = null
    var dataList: T? = null
    var page:String?= null

    companion object {
        /*#异常国际化
    10000=操作成功。
    10001=操作失败。
    10002=获取token成功。
    100021=请求token授权过期。
    100022=验证码失效。
    10003=服务器异常，请重新再刷新。
    10004=请求的参数{0}无效。
    100041=请求的参数{0}不能为空。*/

        val CODE_SUCCESS = "10000"
        val CODE_GET_TOKEN_SUCCESS = "10002"
        val CODE_JSON_PARSE_ERR = "-2"
        val CODE_SESSION_EXPIRED = "10004"
        val CODE_ACCOUNT_EXCEPTION = "7777"
    }
}