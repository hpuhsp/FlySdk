package com.fly.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.chivalrous.sdk.ext.initCustomActionBar
import com.fly.sdk.databinding.ActivityTestToolbarBinding
import com.swallow.fly.base.view.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseActivity<TestBaseViewModel, ActivityTestToolbarBinding>() {
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TestActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override val bindingInflater: (LayoutInflater) -> ActivityTestToolbarBinding
        get() = ActivityTestToolbarBinding::inflate
    
    override val modelClass: Class<TestBaseViewModel>
        get() = TestBaseViewModel::class.java
    
    override fun initView(savedInstanceState: Bundle?) {
        initCustomActionBar(
            binding.titleBar.toolbar,
            true,
            "猪猪侠",
            R.color.text_white,
            R.color.general_green
        )
    }
    
    override fun initData(savedInstanceState: Bundle?) {
    }
    
    
    override fun getStatusBarColor(): Int {
        return R.color.general_green
    }
}