package com.chivalrous.sdk.network

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2021/3/4 13:11
 * @UpdateRemark:
 */
class BaseResult<T> {
    var code = 0
    var msg: String? = null
    var data: T? = null
        private set
    
    fun setData(data: T) {
        this.data = data
    }
    
    val isSuccessful: Boolean
        get() = 0 == code
}