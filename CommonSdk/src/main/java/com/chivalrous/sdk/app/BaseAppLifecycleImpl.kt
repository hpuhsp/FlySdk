package com.chivalrous.sdk.app

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.ProcessUtils
import com.chivalrous.sdk.app.di.AppBaseModule
import com.chivalrous.sdk.cache.AppLocalResource
import com.chivalrous.sdk.utils.AppUtils
import com.swallow.fly.base.app.AppLifecycle
import com.swallow.fly.ext.logd
import com.swallow.fly.utils.SingletonHolderNoneArg
import com.tencent.mmkv.MMKV
import javax.inject.Singleton


/**
 * @Description: 子模块生命周期实现类（观察者模式），可配置需要单独的初始化内容
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/30 9:08
 * @UpdateRemark:   更新说明：
 */
@Singleton
class BaseAppLifecycleImpl : AppLifecycle {
    private lateinit var userDeviceID: String
    private lateinit var localResource: AppLocalResource
    override fun attachBaseContext(base: Context) {
    
    }
    
    /**
     * 配置基础模块初始化信息
     */
    override fun onCreate(application: Application) {
        logd { "------------CurrentProcessName--------->${ProcessUtils.getCurrentProcessName()}" }
        this.localResource = AppLocalResource.getInstance(
            application.getSharedPreferences(
                AppBaseModule.SP_NAME,
                Context.MODE_PRIVATE
            )
        )
//        initMMKV(application)
        initMobileUDID(application)
    }
    
    /**
     * 初始化MMKV
     */
    private fun initMMKV(application: Application) {
        val rootDir: String = MMKV.initialize(application)
        println("mmkv root: $rootDir")
    }
    
    /**
     * 初始化设备唯一标识
     */
    private fun initMobileUDID(context: Context) {
        userDeviceID = AppUtils.getMobileUDID(context)
    }
    
    /**
     * 获取设备唯一标识
     */
    fun getUDID(): String {
        return userDeviceID
    }
    
    /**
     * 是否登录
     */
    fun isLogin(): Boolean {
        return localResource.isLogin()
    }
    
    /**
     * 退出APP
     */
    override fun onTerminate(application: Application) {
    }
    
    fun getVersionName(): String {
        return com.blankj.utilcode.util.AppUtils.getAppVersionName()
    }
    
    companion object :
        SingletonHolderNoneArg<BaseAppLifecycleImpl>(::BaseAppLifecycleImpl)
}