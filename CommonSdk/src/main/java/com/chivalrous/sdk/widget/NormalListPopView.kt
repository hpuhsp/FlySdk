package com.chivalrous.sdk.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chivalrous.sdk.R
import com.chivalrous.sdk.databinding.NormalListPopLayoutBinding

/**
 * @Description: 通用列表Pop
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/22 19:05
 * @UpdateRemark:   更新说明：
 */
class NormalListPopView<T : Any>(context: Context) : PopupWindow() {
    private var binding: NormalListPopLayoutBinding =
        NormalListPopLayoutBinding.inflate(LayoutInflater.from(context))
    
    private var mAdapter: BaseQuickAdapter<T, BaseViewHolder>? = null
    
    init {
        this.contentView = binding.root
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        this.isOutsideTouchable = true
        this.isFocusable = true
        this.animationStyle = R.style.fade_only_pop_animation
        val dw = ColorDrawable(0x60000000)
        setBackgroundDrawable(dw)
        binding.popRootView.setOnClickListener { this.dismiss() }
        initRecyclerView(binding, context)
    }
    
    private fun initRecyclerView(binding: NormalListPopLayoutBinding, context: Context) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.addItemDecoration(
            MyDividerItemDecoration.Builder(context)
                .colorResId(R.color.divide_line)
                .sizeResId(R.dimen.divide_normal)
                .showLastDivider()
                .build()
        )
    }
    
    /**
     * 设置标题
     */
    fun setTitle(title: String) {
        binding.tvTitle.text = title ?: ""
    }
    
    /**
     * 设置列表适配器
     */
    fun setListDataAdapter(adapter: BaseQuickAdapter<T, BaseViewHolder>) {
//        adapter.setOnItemClickListener { adapter, _, position ->
//            mListener?.onItemClick(adapter.data[position] as T)
//        }
        mAdapter = adapter
        binding.recyclerView.adapter = adapter
    }
    
    /**
     * 设置列表适配器
     */
    fun setListDataAdapter(
        adapter: BaseQuickAdapter<T, BaseViewHolder>,
        listener: OnPopItemClickListener<T>?
    ) {
        adapter.setOnItemClickListener { adapter, _, position ->
            listener?.onItemClick(adapter.data[position] as T)
            dismiss()
        }
        mAdapter = adapter
        binding.recyclerView.adapter = adapter
    }
    
    /**
     * 刷新当前列表
     */
    fun notifyListAdapter(list: List<T>) {
        if (!list.isNullOrEmpty()) {
            mAdapter?.setList(list)
        }
    }
    
    /**
     * 当前页面数据是否为空
     */
    fun popIsNotEmpty(): Boolean {
        return mAdapter?.data?.size ?: 0 > 0
    }
    
    private var mListener: OnPopItemClickListener<T>? = null
    fun addOnPopItemClickListener(listener: OnPopItemClickListener<T>) {
        this.mListener = listener
    }
    
    interface OnPopItemClickListener<T> {
        fun onItemClick(item: T)
    }
    
    /**
     * 默认内部实现Adapter
     */
    abstract class PopListAdapter<T>() : BaseQuickAdapter<T, BaseViewHolder>(
        R.layout.item_dialog_list_layout, null
    ) {
        override fun convert(holder: BaseViewHolder, item: T) {
            holder.setText(R.id.tv_name, getDataText(item))
        }
        
        abstract fun getDataText(item: T): String
    }
}

