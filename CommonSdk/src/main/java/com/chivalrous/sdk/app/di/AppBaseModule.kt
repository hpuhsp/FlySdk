package com.chivalrous.sdk.app.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.chivalrous.sdk.cache.AppLocalResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/5/28 17:35
 * @UpdateRemark:   更新说明：
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppBaseModule {
    
    const val SP_NAME = "equipment_sp"
    
    @Provides
    @Singleton
    fun provideAppLocalResource(@AccountSpQualifiter sharedPreferences: SharedPreferences): AppLocalResource {
        return AppLocalResource.getInstance(sharedPreferences)
    }
    
    @AccountSpQualifiter
    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }
}