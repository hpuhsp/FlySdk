package com.chivalrous.sdk.widget.drop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Path
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import java.util.*

/**
 * @Description:悬浮在屏幕上的红点拖拽动画绘制区域
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/10/21 11:45
 * @UpdateRemark:
 */
class DropCover @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    mContext, attrs, defStyleAttr
) {
    interface IDropCompletedListener {
        fun onCompleted(id: Any?, explosive: Boolean)
    }

    private val MAX_RATIO = 0.8f // 固定圆最大的缩放比例
    private val MIN_RATIO = 0.4f // 固定圆最小的缩放比例
    private var DISTANCE_LIMIT = 210 // 固定圆和移动圆的圆心之间的断裂距离
    private var CLICK_DISTANCE_LIMIT = 45 // 不超过此距离视为点击
    private var dropFake: View? = null
    private val path = Path()
    private var radius = 0 // 移动圆形半径
    private var curX = 0f  // 当前手指x坐标
    private var curY = 0f  // 当前手指y坐标
    private var circleX = 0f // 固定圆的圆心x坐标
    private var circleY = 0f // 固定圆的圆心y坐标
    private var ratio = 1f // 圆缩放的比例，随着手指的移动，固定的圆越来越小
    private var needDraw = true // 是否需要执行onDraw方法
    private var hasBroken = false // 是否已经断裂过，断裂过就不需要再画Path了
    private var isDistanceOverLimit = false // 当前移动圆和固定圆的距离是否超过限值
    private var click = true // 是否在点击的距离限制范围内，超过了clickDistance则不属于点击
    private var clickTime: Long = 0 // 记录down的时间点
    private var text: String? = null // 显示的数字
    private var explosionAnim: Array<Bitmap?>? = null   // 爆裂动画位图
    private var explosionAnimStart = false  // 爆裂动画是否开始
    private var explosionAnimNumber = 0 // 爆裂动画帧的个数
    private var curExplosionAnimIndex = 0  // 爆裂动画当前帧
    private var explosionAnimWidth = 0 // 爆裂动画帧的宽度
    private var explosionAnimHeight = 0 // 爆裂动画帧的高度
    private var dropCompletedListeners: MutableList<IDropCompletedListener>? = null // 拖拽动作完成，回调

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制两个圆/Path/文本
        if (needDraw) {
            drawCore(canvas)
        }
        // 爆裂动画
        if (explosionAnimStart) {
            drawExplosionAnimation(canvas)
        }
    }

    private fun drawCore(canvas: Canvas) {
        if (!needDraw) {
            return
        }
        val circlePaint = DropManager.getInstance(mContext).circlePaint
        // 画固定圆（如果已经断裂过了，就不需要画固定圆了）
        if (!hasBroken && !isDistanceOverLimit) {
            circlePaint?.let { canvas.drawCircle(circleX, circleY, radius * ratio, it) }
        }

        // 画移动圆和连线（如果已经断裂过了，就不需要再画Path了）
        if (curX != 0f && curY != 0f) {
            circlePaint?.let { canvas.drawCircle(curX, curY, radius.toFloat(), it) }
            if (!hasBroken && !isDistanceOverLimit) {
                drawPath(canvas)
            }
        }

        // 数字要最后画，否则会被连线遮掩
        if (!TextUtils.isEmpty(text)) {
            val textMove = DropManager.getInstance(mContext).textYOffset
            val textPaint = DropManager.getInstance(mContext).textPaint
            if (curX != 0f && curY != 0f) {
                // 移动圆里面的数字
                textPaint?.let { canvas.drawText(text!!, curX, curY + textMove, it) }
            } else {
                // 只有初始时需要绘制固定圆里面的数字
                textPaint?.let { canvas.drawText(text!!, circleX, circleY + textMove, it) }
            }
        }
    }

    /**
     * 画固定圆和移动圆之间的连线
     */
    private fun drawPath(canvas: Canvas) {
        path.reset()
        val distance = distance(circleX, circleY, curX, curY).toFloat() // 移动圆和固定圆圆心之间的距离
        val sina = (curY - circleY) / distance // 移动圆圆心和固定圆圆心之间的连线与X轴相交形成的角度的sin值
        val cosa = (circleX - curX) / distance // 移动圆圆心和固定圆圆心之间的连线与X轴相交形成的角度的cos值
        val AX = circleX - sina * radius * ratio
        val AY = circleY - cosa * radius * ratio
        val BX = circleX + sina * radius * ratio
        val BY = circleY + cosa * radius * ratio
        val OX = (circleX + curX) / 2
        val OY = (circleY + curY) / 2
        val CX = curX + sina * radius
        val CY = curY + cosa * radius
        val DX = curX - sina * radius
        val DY = curY - cosa * radius
        path.moveTo(AX, AY) // A点坐标
        path.lineTo(BX, BY) // AB连线
        path.quadTo(OX, OY, CX, CY) // 控制点为两个圆心的中间点，贝塞尔曲线，BC连线
        path.lineTo(DX, DY) // CD连线
        path.quadTo(OX, OY, AX, AY) // 控制点也是两个圆心的中间点，贝塞尔曲线，DA连线
        DropManager.getInstance(mContext).circlePaint?.let { canvas.drawPath(path, it) }
    }

    /**
     * ************************* TouchListener回调 *************************
     */
    fun down(fakeView: View?, text: String?) {
        needDraw = true // 由于DropCover是公用的，每次进来时都要确保needDraw的值为true
        hasBroken = false // 未断裂
        isDistanceOverLimit = false // 当前移动圆和固定圆的距离是否超过限值
        click = true // 点击开始
        dropFake = fakeView
        val position = IntArray(2)
        dropFake!!.getLocationOnScreen(position)
        radius = DropManager.getInstance(mContext).circleRadius
        // 固定圆圆心坐标，固定圆圆心坐标y，需要减去系统状态栏高度
        circleX = position[0] + dropFake!!.width / 2.toFloat()
        circleY =
            position[1] - DropManager.getInstance(mContext).top + dropFake!!.height / 2.toFloat()
        // 移动圆圆心坐标
        curX = circleX
        curY = circleY
        this.text = text
        clickTime = System.currentTimeMillis()

        // hide fake view, show current
        dropFake!!.visibility = INVISIBLE // 隐藏固定范围的DropFake
        this.visibility = VISIBLE // 当前全屏范围的DropCover可见
        invalidate()
    }

    fun move(curX: Float, curY: Float) {
        var curY = curY
        curY -= DropManager.getInstance(mContext).top.toFloat() // 位置校准，去掉通知栏高度
        this.curX = curX
        this.curY = curY
        calculateRatio(distance(curX, curY, circleX, circleY).toFloat()) // 计算固定圆缩放的比例
        invalidate()
    }

    /**
     * 计算固定圆缩放的比例
     */
    private fun calculateRatio(distance: Float) {
        if ((distance > DISTANCE_LIMIT) == isDistanceOverLimit) {
            hasBroken = true // 已经断裂过了
        }

        // 固定圆缩放比例0.4-0.8之间
        ratio = MIN_RATIO + (MAX_RATIO - MIN_RATIO) * (1.0f * Math.max(
            DISTANCE_LIMIT - distance,
            0f
        )) / DISTANCE_LIMIT
    }

    fun up() {
        val longClick =
            click && System.currentTimeMillis() - clickTime > CLICK_DELTA_TIME_LIMIT // 长按

        // 没有超出最大移动距离&&不是长按点击事件，UP时需要让移动圆回到固定圆的位置
        if (!isDistanceOverLimit && !longClick) {
            if (hasBroken) {
                // 如果已经断裂，那么直接回原点，显示FakeView
                onDropCompleted(false)
            } else {
                // 如果还未断裂，那么执行抖动动画
                shakeAnimation()
            }
            // reset
            curX = 0f
            curY = 0f
            ratio = 1f
        } else {
            // 超出最大移动距离，那么执行爆裂帧动画
            initExplosionAnimation()
            needDraw = false
            explosionAnimStart = true
        }
        invalidate()
    }

    fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1).toDouble())
        if (distance > CLICK_DISTANCE_LIMIT) {
            click = false // 已经不是点击了
        }
        return distance
    }

    /**
     * ************************* 爆炸动画(帧动画) *************************
     */
    private fun initExplosionAnimation() {
        if (explosionAnim == null) {
            val explosionResIds = DropManager.getInstance(mContext).explosionResIds
            explosionAnimNumber = explosionResIds.size
            explosionAnim = arrayOfNulls<Bitmap>(explosionAnimNumber)
            for (i in 0 until explosionAnimNumber) {
                explosionAnim!![i] = BitmapFactory.decodeResource(resources, explosionResIds[i])
            }
            explosionAnimWidth = explosionAnim!![0]!!.width
            explosionAnimHeight = explosionAnimWidth // 每帧长宽都一致
        }
    }

    private fun drawExplosionAnimation(canvas: Canvas) {
        if (!explosionAnimStart) {
            return
        }
        if (curExplosionAnimIndex < explosionAnimNumber) {
            canvas.drawBitmap(
                explosionAnim!![curExplosionAnimIndex]!!,
                curX - explosionAnimWidth / 2, curY - explosionAnimHeight / 2, null
            )
            curExplosionAnimIndex++
            // 每隔固定时间执行
            postInvalidateDelayed(EXPLOSION_ANIM_FRAME_INTERVAL.toLong())
        } else {
            // 动画结束
            explosionAnimStart = false
            curExplosionAnimIndex = 0
            curX = 0f
            curY = 0f
            onDropCompleted(true) // explosive true
        }
    }

    private fun recycleBitmap() {
        if (explosionAnim != null && explosionAnim!!.size != 0) {
            for (i in explosionAnim!!.indices) {
                if (explosionAnim!![i] != null && !explosionAnim!![i]!!.isRecycled) {
                    explosionAnim!![i]!!.recycle()
                    explosionAnim!![i] = null
                }
            }
            explosionAnim = null
        }
    }

    /**
     * ************************* 抖动动画(View平移动画) *************************
     */
    fun shakeAnimation() {
        // 避免动画抖动的频率过大，所以除以10，另外，抖动的方向跟手指滑动的方向要相反
        val translateAnimation: Animation =
            TranslateAnimation((circleX - curX) / 10, 0f, (circleY - curY) / 10, 0f)
        translateAnimation.interpolator = CycleInterpolator(1f)
        translateAnimation.duration = SHAKE_ANIM_DURATION.toLong()
        startAnimation(translateAnimation)
        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // 抖动动画结束时，show Fake, hide current
                onDropCompleted(false)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * ************************* 拖拽动作结束事件 *************************
     */
    fun addDropCompletedListener(listener: IDropCompletedListener?) {
        if (listener == null) {
            return
        }
        if (dropCompletedListeners == null) {
            dropCompletedListeners = ArrayList(1)
        }
        dropCompletedListeners!!.add(listener)
    }

    fun removeDropCompletedListener(listener: IDropCompletedListener?) {
        if (listener == null || dropCompletedListeners == null) {
            return
        }
        dropCompletedListeners!!.remove(listener)
    }

    fun removeAllDropCompletedListeners() {
        if (dropCompletedListeners == null) {
            return
        }
        dropCompletedListeners!!.clear()
    }

    private fun onDropCompleted(explosive: Boolean) {
        dropFake!!.visibility = if (explosive) INVISIBLE else VISIBLE // show or hide fake view
        this.visibility = INVISIBLE // hide current
        recycleBitmap() // recycle

        // notify observer
        if (dropCompletedListeners != null) {
            for (listener in dropCompletedListeners!!) {
                listener.onCompleted(DropManager.getInstance(mContext).currentId, explosive)
            }
        }

        // free
        DropManager.getInstance(mContext).isTouchable = true
    }

    companion object {
        private const val SHAKE_ANIM_DURATION = 150 // 抖动动画执行的时间
        private const val EXPLOSION_ANIM_FRAME_INTERVAL = 50 // 爆裂动画帧之间的间隔
        private const val CLICK_DELTA_TIME_LIMIT = 10 // 超过此时长需要爆裂
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    /**
     * ************************* 绘制 *************************
     */
    init {
        DISTANCE_LIMIT = dip2px(mContext, 70f)
        CLICK_DISTANCE_LIMIT = dip2px(mContext, 15f)
        DropManager.getInstance(mContext).initPaint()
    }
}