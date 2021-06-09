package com.chivalrous.sdk.widget.drop

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.view.View
import com.chivalrous.sdk.R
import com.chivalrous.sdk.widget.drop.DropCover.IDropCompletedListener

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/10/21 11:57
 * @UpdateRemark:
 */
class DropManager {
    private var TEXT_SIZE = 12 // 默认设置 12sp
    var circleRadius = 10 // 默认设置 10dip
        private set

    interface IDropListener {
        fun onDropBegin()
        fun onDropEnd()
    }

    private fun initSize(context: Context) {
        TEXT_SIZE = sp2px(context, 10)
        circleRadius = sp2px(context, 9)
    }

    // 是否响应按键事件，如果一个红点已经在响应，其它红点就不响应，同一界面始终最多只有一个红点响应触摸
    var isTouchable = false

    // 状态栏(通知栏)高度
    // Drop全屏动画
    var top = 0
        private set
    private var dropCover: DropCover? = null

    // 当前正在执行动画的业务节点
    var currentId: Any? = null

    // 文本画笔共享
    var textPaint: TextPaint? = null

    // 文本y轴居中需要的offset
    var textYOffset = 0f

    // 圆形画笔共享
    var circlePaint: Paint? = null

    // 红点拖拽动画监听器
    private var listener: IDropListener? = null
    var isEnable = false
        private set
    val explosionResIds = intArrayOf( // 爆裂的动画效果
        R.mipmap.nim_explosion_one,
        R.mipmap.nim_explosion_two,
        R.mipmap.nim_explosion_three,
        R.mipmap.nim_explosion_four,
        R.mipmap.nim_explosion_five
    )

    // interface
    fun init(
        context: Context?,
        dropCover: DropCover?,
        listener: IDropCompletedListener?,
        topDistance: Int
    ) {
        isTouchable = true
        top = topDistance
        this.dropCover = dropCover
        this.dropCover!!.addDropCompletedListener(listener)
        this.listener = null
        isEnable = true
    }

    fun initPaint() {
        getDropCirclePaint()
        getDropTextPaint()
    }

    fun destroy() {
        isTouchable = false
        top = 0
        if (dropCover != null) {
            dropCover!!.removeAllDropCompletedListeners()
            dropCover = null
        }
        currentId = null
        textPaint = null
        textYOffset = 0f
        circlePaint = null
        isEnable = false
    }

    fun isDropTouchable(): Boolean {
        return if (!isEnable) {
            true
        } else isTouchable
    }

    fun setDropTouchable(isTouchable: Boolean) {
        this.isTouchable = isTouchable
        if (listener != null) {
            if (!isTouchable) {
                listener!!.onDropBegin() // touchable = false
            } else {
                listener!!.onDropEnd() // touchable = true
            }
        }
    }

    fun down(fakeView: View?, text: String?) {
        if (dropCover == null) {
            return
        }
        dropCover!!.down(fakeView, text)
    }

    fun move(curX: Float, curY: Float) {
        if (dropCover == null) {
            return
        }
        dropCover!!.move(curX, curY)
    }

    fun up() {
        if (dropCover == null) {
            return
        }
        dropCover!!.up()
    }

    fun addDropCompletedListener(listener: IDropCompletedListener?) {
        if (dropCover != null) {
            dropCover!!.addDropCompletedListener(listener)
        }
    }

    fun removeDropCompletedListener(listener: IDropCompletedListener?) {
        if (dropCover != null) {
            dropCover!!.removeDropCompletedListener(listener)
        }
    }

    fun getDropCirclePaint(): Paint {
        if (circlePaint == null) {
            circlePaint = Paint()
            circlePaint!!.color = Color.parseColor("#F15660") // 红色背景
            circlePaint!!.isAntiAlias = true
        }
        return circlePaint!!
    }

    fun getDropTextPaint(): TextPaint {
        if (textPaint == null) {
            textPaint = TextPaint()
            textPaint!!.isAntiAlias = true
            textPaint!!.color = Color.WHITE // 白色字体
            textPaint!!.textAlign = Paint.Align.CENTER
            textPaint!!.textSize = TEXT_SIZE.toFloat()
            val textFontMetrics = textPaint!!.fontMetrics

            /*
             * drawText从baseline开始，baseline的值为0，baseline的上面为负值，baseline的下面为正值，
             * 即这里ascent为负值，descent为正值。
             * 比如ascent为-20，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2
             */textYOffset =
                -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2
        }
        return textPaint!!
    }

    fun getDropTextYOffset(): Float {
        getDropTextPaint()
        return textYOffset
    }

    fun setDropListener(listener: IDropListener?) {
        this.listener = listener
    }

    companion object {
        // constant
        private const val TAG = "DropManager"

        private var instance: DropManager? = null

        @Synchronized
        fun getInstance(context: Context): DropManager {
            if (instance == null) {
                instance = DropManager()
                instance!!.initSize(context)
            }
            return instance!!
        }

        /**
         * sp 转 px
         *
         * @param context [Context]
         * @param spValue `spValue`
         * @return `pxValue`
         */
        private fun sp2px(context: Context, spValue: Int): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}