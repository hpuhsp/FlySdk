package com.chivalrous.sdk.cache

import android.content.SharedPreferences
import com.chivalrous.sdk.config.ApiConfig
import com.swallow.fly.ext.logd
import com.swallow.fly.ext.prefs.boolean
import com.swallow.fly.ext.prefs.int
import com.swallow.fly.ext.prefs.string
import com.swallow.fly.utils.SingletonHolderSingleArg
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/5/28 17:53
 * @UpdateRemark:   更新说明：
 */
@Singleton
class AppLocalResource @Inject constructor(private val prefs: SharedPreferences) {
    
    //=========================================本地网络配置========================================//
    /**
     * 保存本地网络配置
     */
    fun saveNetWorkConfig(
        apiHost: String,
        fileHost: String,
        enterpriseId: Int,
        posNo: String,
        pdaNo: String
    ) {
        baseUrl = apiHost
        fileUrl = fileHost
        
        this.enterpriseId = enterpriseId
        this.posMachineCode = posNo
        this.pdaNo = pdaNo
    }
    
    private var baseUrl by prefs.string(ApiConfig.APP_URL, "")
    
    var fileUrl by prefs.string(ApiConfig.FILE_URL, "")
    
    //============================================基础信息========================================//
    
    private var token by prefs.string(
        "access_token",
        ""
    )
    
    private var isAutoLogin: Boolean by prefs.boolean("login_state", false)
    
    var userCode by prefs.string("user_code", "")
    var userName by prefs.string("user_name", "")
    var password by prefs.string("password", "")
    var orgCode by prefs.string("org_code", "")
    var orgName by prefs.string("org_name", "")
    var orgId by prefs.int("org_id")
    var userId by prefs.int("user_id")
    var mobilephone by prefs.string("mobile_phone", "")
    
    // App 1.3.0融合肉制品程序添加
    var groupCode by prefs.string("group_code", "")
    var groupName by prefs.string("group_name", "")
    var posMachineCode by prefs.string("posMachineCode", "") // Pos机号
    var serviceTime by prefs.string("service_time", "") // 登录时间
    var flightsCode by prefs.string("flights_code", "")
    var flightsName by prefs.string("flights_name", "")
    var enterpriseId by prefs.int("enterprise_id")
    var settlementState by prefs.string("settlement_state", "")
    var settlementDay by prefs.string("settlement_day", "")
    var loginTime by prefs.string("login_time", "")
    var pdaNo by prefs.string("pda_no", "") // PDA编码
    
    // 重新设置网络配置信息
    var resetApiConfig: Boolean by prefs.boolean("reset_api", true)
    var offline by prefs.string("offline_no", "N") // Y 为离线
    var musicSetting by prefs.int("music_setting") // Y 为离线
    //============================================公共方法========================================//
    
    fun saveInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
    
    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }
    
    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    fun getString(key: String): String {
        return prefs.getString(key, "") ?: ""
    }
    
    fun saveLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }
    
    fun getLong(key: String): Long {
        return prefs.getLong(key, Long.MAX_VALUE)
    }
    
    //===========================================自定义方法========================================//
    /**
     * 保存用户信息
     */
    fun saveToken(access_token: String) {
        token = access_token
        isAutoLogin = true
    }
    
    /**
     * 判断是否登录
     */
    fun isLogin(): Boolean {
        return token?.isNotEmpty() && isAutoLogin
    }
    
    /**
     * 清空用户信息
     */
    fun emptyUserInfo() {
        token = ""
        isAutoLogin = false
    }
    
    /**
     * 清除当前网络配置信息
     */
    fun emptyNetworkConfigInfo() {
        baseUrl = ""
        fileUrl = ""
    }
    
    /**
     * 判断当前网络配置信息
     */
    fun networkConfigIsEmpty(): Boolean {
        return baseUrl.isNullOrEmpty() || fileUrl.isNullOrEmpty()
    }
    
    
    companion object :
        SingletonHolderSingleArg<AppLocalResource, SharedPreferences>(::AppLocalResource)
}