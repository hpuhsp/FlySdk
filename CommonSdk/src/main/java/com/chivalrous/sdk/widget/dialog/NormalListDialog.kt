package com.chivalrous.sdk.widget.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chivalrous.sdk.R
import com.chivalrous.sdk.databinding.ListDialogLayoutBinding
import com.chivalrous.sdk.widget.MyDividerItemDecoration
import com.swallow.fly.utils.FastUtils

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/2/9 10:13
 * @UpdateRemark:   更新说明：
 */
class NormalListDialog<T>(context: Context) : AppCompatDialog(context) {
    val binding = ListDialogLayoutBinding.inflate(LayoutInflater.from(context))
    private var mAdapter: ListAdapter<T>? = null
    
    init {
        initDialog()
    }
    
    private fun initDialog() {
        this.setContentView(binding.root)
        val window = window
        if (null != window) {
            val dialogWidth = (FastUtils.getScreenWidth(context) * 0.75).toInt()
            window.setGravity(Gravity.CENTER)
            window.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }
    
    class Builder<T>(context: Context) {
        var title: String? = null
        var itemDecoration: MyDividerItemDecoration? = null
        var adapter: ListAdapter<T>? = null
        private val dialog = NormalListDialog<T>(context)
        fun setTitle(text: String?): Builder<T> {
            title = text
            return this
        }
        
        fun setDivideLine(itemDecoration: MyDividerItemDecoration?): Builder<T> {
            this.itemDecoration = itemDecoration
            return this
        }
        
        fun setAdapter(adapter: ListAdapter<T>): Builder<T> {
            this.adapter = adapter
            return this
        }
        
        fun create(): NormalListDialog<T> {
            dialog.initView(this)
            return dialog
        }
    }
    
    private fun initView(builder: Builder<T>) {
        val title = builder.title
        if (title.isNullOrEmpty()) {
            binding.tvTitle.visibility = View.GONE
        } else {
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvTitle.text = title
        }
        builder.itemDecoration?.apply {
            binding.recyclerView.addItemDecoration(this)
        }
        mAdapter = builder.adapter
        mAdapter?.setOnItemClickListener { adapter, view, position -> }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
    }
    
    /**
     * 设置数据
     */
    fun notifyListData(list: List<T>) {
        mAdapter?.setList(list)
    }
    
    fun isEmpty(): Boolean {
        return mAdapter?.data.isNullOrEmpty()
    }
    
    /**
     * 默认选中位置
     */
    
    abstract class ListAdapter<T> :
        BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_list_dialog_layout, null) {
        
        override fun convert(holder: BaseViewHolder, item: T) {
            holder.setText(R.id.tv_desc, getText(item))
        }
        
        override fun setOnItemClick(v: View, position: Int) {
            onListItemSelect(data[position], position)
        }
        
        abstract fun onListItemSelect(item: T, position: Int)
        
        abstract fun getText(item: T): String
    }
}