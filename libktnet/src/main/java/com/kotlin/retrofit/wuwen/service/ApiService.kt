package com.kotlin.retrofit.wuwen.service


import com.kotlin.retrofit.wuwen.http.HttpResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    //    @Headers("Content-Type: application/json")
    @POST("/cdp/rest/token/appAccessTokenService/app/getUserAccessToken")
    fun userToken(@Body body: RequestBody): Observable<HttpResponse<Any?>>


}