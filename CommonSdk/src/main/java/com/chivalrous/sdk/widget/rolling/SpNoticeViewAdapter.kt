package com.hnsh.resource.widget

import android.view.View
import com.chivalrous.sdk.widget.rolling.SpNoticeView

/**
 * @Description: 公告滚动视图适配器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/29 16:59
 * @UpdateRemark:   更新说明：
 */
abstract class SpNoticeViewAdapter {
    /**
     * 获取数据的条数
     *
     * @return
     */
    abstract fun getCount(): Int

    /**
     * 获取摸个数据
     *
     * @param position
     * @return
     */
    abstract fun getItem(position: Int): Any

    /**
     * 获取条目布局
     *
     * @param parent:      父控件
     * @param contentView: 缓存的视图项
     * @return
     */
    abstract fun getView(parent: SpNoticeView, contentView: View): View

    /**
     * 条目数据适配
     *
     * @param view: 视图
     * @param data: 对应的数据项
     */
    abstract fun setItem(view: View, data: Any)

    abstract fun removeListener()

}