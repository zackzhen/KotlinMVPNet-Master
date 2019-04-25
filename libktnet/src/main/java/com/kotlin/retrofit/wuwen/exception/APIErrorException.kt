package  com.kotlin.retrofit.http

/**
 * API error exception
 **/
data class APIErrorException(var url: String? = null,
                             var method: String? = null,
                             val query: String? = null,
                             var statusCode: String? = null,
                             var error: String? = null): RuntimeException(error) {
}