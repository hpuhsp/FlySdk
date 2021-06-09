package com.fly.sdk.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.swallow.fly.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/25 14:11
 * @UpdateRemark:   更新说明：
 */
class MainViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    BaseViewModel() {

    fun test() {
        viewModelScope.launch {

        }
    }
}