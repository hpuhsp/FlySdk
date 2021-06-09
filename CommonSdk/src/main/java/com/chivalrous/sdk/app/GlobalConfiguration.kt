package com.chivalrous.sdk.app

import android.content.Context
import com.chivalrous.sdk.app.di.AppBaseModule
import com.chivalrous.sdk.config.ApiConfig
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.base.app.ConfigModule
import com.swallow.fly.base.app.config.GlobalConfigModule
import com.swallow.fly.ext.logd
import javax.inject.Singleton

/**
 * @Description: 全局初始化配置
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2021/5/27 18:48
 * @UpdateRemark:
 */
@Singleton
class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
    }
    
    /**
     * 添加子Module的生命周期监听
     */
    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {
        lifecycleList.add(BaseAppLifecycleImpl.getInstance())
    }
}