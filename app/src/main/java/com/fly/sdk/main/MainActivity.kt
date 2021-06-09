package com.fly.sdk.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.chivalrous.sdk.ext.initCustomActionBar
import com.fly.sdk.R
import com.fly.sdk.TestActivity
import com.fly.sdk.databinding.ActivityMainBinding
import com.swallow.fly.base.view.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    
    override fun initView(savedInstanceState: Bundle?) {
        // 此处是include方式引入CommonSdk Module 下的布局文件，因此无法使用kotlin-android-extensions的方式引用id
        initCustomActionBar(
            findViewById(R.id.toolbar),
            true,
            "猪猪侠",
            R.color.text_white,
            R.color.light_blue
        )
        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            TestActivity.start(this@MainActivity)
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun initData(savedInstanceState: Bundle?) {
    }
    
    override fun getStatusBarColor(): Int {
        return R.color.light_blue
    }
    
    override fun showDarkToolBar(): Boolean {
        return false
    }
    
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
}