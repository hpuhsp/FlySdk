package com.chivalrous.sdk.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2021/3/4 13:10
 * @UpdateRemark:
 */
abstract class BaseCallback<T : BaseResult<*>> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful && null != response.body()) {
            onRequestSuccess(response.body()!!)
        } else {
            onRequestFailure()
        }
    }
    
    override fun onFailure(call: Call<T>, t: Throwable) {
        if (t is ConnectException) {
            onRequestNetWorkFailure()
        }
        onRequestFailure()
    }
    
    abstract fun onRequestSuccess(baseResult: T)
    abstract fun onRequestFailure()
    abstract fun onRequestNetWorkFailure()
}