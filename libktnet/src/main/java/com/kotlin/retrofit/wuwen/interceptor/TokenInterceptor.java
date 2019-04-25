package com.kotlin.retrofit.wuwen.interceptor;


import android.util.Log;

import com.google.gson.Gson;
import com.kotlin.retrofit.wuwen.util.Constant;
import com.kotlin.retrofit.wuwen.util.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();


        // 新的请求,添加参数
        Request newRequest = addParam(oldRequest, true);

        Response  response = chain.proceed(newRequest);

        ResponseBody value = response.body();
        byte[] resp = value.bytes();
        String json = new String(resp, "UTF-8");

        // 判断stateCode值
        try {
            JSONObject jsonObject = new JSONObject(json);
            int stateCode = jsonObject.optInt("code");
            if (stateCode == 10004) {
                // token失效，重新执行请求
                Request newTokenRequest = addParam(oldRequest, true);

                response = chain.proceed(newTokenRequest);
            } else {
                // 这里值得注意。由于前面value.bytes()把响应流读完并关闭了，所以这里需要重新生成一个response，否则数据就无法正常解析了
                response = response.newBuilder().body(ResponseBody.create(null, resp)).build();
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return response;
    }

    /**
     * 添加公共参数
     *
     * @param oldRequest
     * @return
     */
    private Request addParam(Request oldRequest, boolean getNewToken) {



        if (getNewToken) {
            token = postRequest();

//            UserManager.Companion.setUserToken(token);
        }
        HttpUrl.Builder builder = oldRequest.url()
                .newBuilder();

        Request newRequest = oldRequest.newBuilder().header("accessToken", token)
                .method(oldRequest.method(), oldRequest.body())
                .url(builder.build())
                .build();

        return newRequest;
    }


    //初始化okHttpClient
    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();

    }

    private OkHttpClient mOkHttpClient;
    private String token = null;

    public String postRequest() {


        //请求方法
        try {
            initOkHttpClient();
            HashMap map = new HashMap<String, String>();
            map.put("userId", "12345678901");
            map.put("requestType", "Android");

            Gson gson = new Gson();
            String postInfoStr = gson.toJson(map);

            //去请求
            Request request = new Request.Builder()
                    .url(Constant.Companion.getBASE_API_SERVER_URL()+ "/cdp/rest/token/appAccessTokenService/app/getUserAccessToken")
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfoStr))
                    .build();

            Response response = mOkHttpClient.newCall(request).execute();
            //String result  = response.body().string();
            String body = response.body().string();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(body);
                token = jsonObject.optString("data");
//                UserManager.Companion.setUserToken(jsonObject.optString("data"));

                KLog.Companion.e("获取新的token");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
}
