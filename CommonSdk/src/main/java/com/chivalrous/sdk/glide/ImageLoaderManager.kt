package com.chivalrous.sdk.glide

import android.content.Context
import android.widget.ImageView
import com.chivalrous.sdk.R
import com.swallow.fly.image.GlideApp
import com.swallow.fly.utils.SingletonHolderNoneArg
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/3 15:44
 * @UpdateRemark:   更新说明：
 */
@Singleton
class ImageLoaderManager {
    /**
     * 初始化配置图片域名
     */
    private var imgHost: String = ""
    fun initApiConfig(host: String) {
        this.imgHost = host
    }
    
    /**
     * 使用默认配置加载普通图（远端类型）
     */
    fun loadImage(context: Context, path: String, imageView: ImageView) {
        GlideApp.with(context).load("http://${imgHost}/${path}")
            .error(R.mipmap.img_empty)
            .into(imageView)
    }
    
    /**
     * 使用默认配置加载普通图（远端类型）
     */
    fun loadLocalImage(context: Context, path: String, imageView: ImageView) {
        GlideApp.with(context).load(path)
            .error(R.mipmap.img_empty)
            .into(imageView)
        
    }
    
    fun getRemotePath(path: String): String {
        return "http://${imgHost}/${path}"
    }
    
    companion object :
        SingletonHolderNoneArg<ImageLoaderManager>(::ImageLoaderManager)
}