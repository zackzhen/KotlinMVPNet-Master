package com.wuwen.mvp.demo


import com.kotlin.retrofit.wuwen.http.HttpResponse
import com.wuwen.mvp.demo.data.UserBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    //    @Headers("Content-Type: application/json")
    @POST("/cdp/rest/token/appAccessTokenService/app/getUserAccessToken")
    fun userToken(@Body body: RequestBody): Observable<HttpResponse<Any?>>

    @POST("user/add")
     fun addUser(@Query("name") userName: String,
                         @Query("imei") imei: String,
                         @Query("pushToken") pushToken: String
    ): Observable<HttpResponse<Any>>


    @POST("user/list")
     fun getUserList(@Body body: RequestBody): Observable<HttpResponse<List<UserBean>>>


    @POST("push")
     fun startPush(@Body body: RequestBody): Observable<HttpResponse<Any>>

}