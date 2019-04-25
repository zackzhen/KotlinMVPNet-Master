package  com.kotlin.retrofit.wuwen.util.http

import com.kotlin.retrofit.http.APIErrorException
import com.kotlin.retrofit.wuwen.util.KLog
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import java.io.IOException

/**
 * API error interceptor
 * 拦截器
 *
 */
class APIErrorInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        var query: String = ""
        val requestBody = request.body()

        if(null != requestBody) {
            if(requestBody is FormBody) {
                (0..requestBody.size() - 1).forEach {
                    query += "${requestBody.encodedName(it)} = ${requestBody.encodedValue(it)}&"
                }
            }
        }

        if (!response.isSuccessful) {
            val url = request.url().toString()
            val method = request.method()
            val statusCode = "${response.code()}"
            var error: String? = null

            if (HttpHeaders.hasBody(response)) {
                val networkResponse = response.networkResponse()
                if(null != networkResponse) {
                    error = networkResponse.toString()
                }
            }

            val apiError = APIErrorException(url = url,
                                             method = method,
                                             query = query,
                                             statusCode = statusCode,
                                             error = error)
            KLog.e(apiError.toString())
        }

        return response
    }
}