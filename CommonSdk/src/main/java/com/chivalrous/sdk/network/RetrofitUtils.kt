package com.swallow.fly.http.custom

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @Description: 自定义网络配置
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2021/3/4 13:26
 * @UpdateRemark:
 */
@Singleton
object RetrofitUtils {

    /**
     * @param clazz
     * @param host
     * @param <T>
     * @return
     */
    fun <T> createApi(clazz: Class<T>, host: String): T {
        val builder = Retrofit.Builder()
        builder.baseUrl(host)
        builder.addConverterFactory(GsonConverterFactory.create())
        builder.client(OkHttpClient().newBuilder().build())
        return builder.build().create(clazz)
    }
}