package com.chivalrous.sdk.ext

import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.blankj.utilcode.util.ColorUtils
import com.chivalrous.sdk.R

/**
 * @Description: 默认白色背景黑色文字的标题栏，返回按钮为黑色
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/12/25 14:00
 * @UpdateRemark:
 */
fun AppCompatActivity.initActionBar(
    @NonNull toolbar: Toolbar,
    @NonNull hasBackButton: Boolean,
    title: String?
) {
    toolbar.title = ""
    setSupportActionBar(toolbar)
    val mActionBar = supportActionBar
    if (hasBackButton) {
        mActionBar?.setHomeAsUpIndicator(R.mipmap.ic_toolbar_back)
        mActionBar?.setDisplayHomeAsUpEnabled(true)
        mActionBar?.setHomeButtonEnabled(true)
    } else {
        mActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        mActionBar?.setDisplayUseLogoEnabled(false)
        mActionBar?.setTitle(title ?: "")
    }
    mActionBar?.setBackgroundDrawable(
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.bg_white_tool_bar,
            null
        )
    )
    val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
    tvTitle.setTextColor(ColorUtils.getColor(R.color.text_black))
    tvTitle.text = title ?: ""
}

/**
 * 自定义标题栏及文字颜色，白色返回按钮
 */
fun AppCompatActivity.initCustomActionBar(
    @NonNull toolbar: Toolbar,
    @NonNull hasBackButton: Boolean,
    title: String?,
    @NonNull txtColorRes: Int,
    @NonNull bgColorRes: Int
) {
    toolbar.title = ""
    setSupportActionBar(toolbar)
    val mActionBar = supportActionBar
    if (hasBackButton) {
        mActionBar?.setHomeAsUpIndicator(R.mipmap.ic_toolbar_up)
        mActionBar?.setDisplayHomeAsUpEnabled(true)
        mActionBar?.setHomeButtonEnabled(true)
    } else {
        mActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        mActionBar?.setDisplayUseLogoEnabled(false)
        mActionBar?.setTitle(title ?: "")
    }
    mActionBar?.setBackgroundDrawable(ColorDrawable(ColorUtils.getColor(bgColorRes)))
    val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
    tvTitle.setTextColor(ColorUtils.getColor(txtColorRes))
    tvTitle.text = title ?: ""
}