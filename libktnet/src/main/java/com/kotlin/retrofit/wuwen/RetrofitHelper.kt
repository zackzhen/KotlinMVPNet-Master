package com.kotlin.retrofit.wuwen

import android.content.Context
import com.google.gson.Gson
import com.kotlin.retrofit.wuwen.util.Constant
import com.kotlin.retrofit.wuwen.util.KLog
import com.kotlin.retrofit.wuwen.util.http.APIErrorInterceptor
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


/**
 * 生成Retrofit实例
 *
 */
object RetrofitHelper {
    private var DEFAULT_TIME_OUT = 60L
    private var gson = Gson()

    private fun getSSLConfig(context: Context): SSLContext? {
        try {
            // Loading CAs from an InputStream
//            val cf = CertificateFactory.getInstance("X.509")

//            var ca: Certificate? = null
            // I'm using Java7. If you used Java6 close it manually with finally.
//            context.resources.openRawResource(ServerConfig.httpsCer()).use({ cert -> ca = cf!!.generateCertificate(cert) })

            // Creating a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
//            keyStore.setCertificateEntry("ca", ca)

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            // Creating an SSLSocketFactory that uses our TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, null)

            return sslContext
        } catch (e: Exception) {

            e.printStackTrace()
            return null
        }
    }

    fun getDefault(): Retrofit {
        val httpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)

//        val socketFactory = getSSLConfig(Application.get())?.socketFactory
//        if(null != socketFactory) {
//            httpClientBuilder.sslSocketFactory(socketFactory)
//        }
//        val cache = Cache(FileManager.httpCacheDir(), 10 * 1024 * 1024)
//        httpClientBuilder.cache(cache)

//        httpClientBuilder.addInterceptor(TokenInterceptor())

        httpClientBuilder.addInterceptor { chain ->
            val request = chain.request()
            val urlBuilder = request.url().newBuilder()

            val newRequestBuilder = request.newBuilder()
            newRequestBuilder.addHeader("charset", "UTF-8") //文本编码
            newRequestBuilder.addHeader("userType", "1")
            newRequestBuilder.addHeader("requestType", "Android")

            chain.proceed(newRequestBuilder.method(request.method(), request.body())
                    .url(urlBuilder.build())
                    .build())
        }
        httpClientBuilder.addInterceptor(APIErrorInterceptor())

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                KLog.d(message)
            })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder().baseUrl(Constant.BASE_API_SERVER_URL)
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    }

    fun getBody(map: HashMap<String, Any>): RequestBody {

        map["requestType"] = "Android"


        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(map))
    }
}