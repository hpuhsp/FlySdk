package com.fly.sdk

import com.swallow.fly.base.BaseApplication
import com.swallow.fly.ext.initLogger
import dagger.hilt.android.HiltAndroidApp

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/25 14:13
 * @UpdateRemark:   更新说明：
 */
@HiltAndroidApp
class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        // 可根据编译环境选择日志打印策略
        initLogger(true)
    }
}