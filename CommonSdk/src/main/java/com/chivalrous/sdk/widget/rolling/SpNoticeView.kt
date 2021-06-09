package com.chivalrous.sdk.widget.rolling

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hnsh.resource.widget.SpNoticeViewAdapter
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * @Description:公告滚动视图
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/29 16:59
 * @UpdateRemark:   更新说明：
 */
class SpNoticeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    /**
     * 动画移动方向: 向上
     */
    val DIRECTION_UP = 1

    /**
     * 动画移动方向: 向下
     */
    val DIRECTION_DOWN = -1

    /**
     * 内存缓存视图队列
     */
    private val cacheViews: Queue<View> = LinkedBlockingQueue()

    /**
     * 控件高度(默认每个Item项的高度和广告栏高度一致),即为单个item的高度
     */
    private var mAdverHeight = 0f

    /**
     * 控件宽度
     */
    private var mAdverWidth = 0f

    /**
     * 视图停留间隔时间,默认为4秒
     */
    private var mGap = 4000

    //动画间隔时间
    private var mAnimDuration = 1500

    /**
     * 滚动列表数据适配器
     */
    private var mAdapter: SpNoticeViewAdapter? = null

    /**
     * 默认广告栏高度
     */
    private val jdAdverHeight = 58f

    //显示的view
    private var mFirstView: View? = null
    private var mSecondView: View? = null

    //播放的下标
    private var mPosition = 0

    //线程开始的标识( 是否开始滚动)
    private var isStarted = false

    /**
     * 是否向上滚动
     */
    private var mAnimDirection = DIRECTION_UP

    //画笔
    private var mPaint //画笔用来绘制文字,此处没用到
            : Paint? = null

    /**
     * 刷新动画线程
     */
//    private var mRunnable: AnimRunnable? = null

    /**
     * 动画对象。动画集合
     */
    private var animatorSet: AnimatorSet? = null

    /**
     * 子项目个数
     */
    private var mItemCount = 0

    /**
     * 内部控制刷新频度防止重复刷新重合(动画启动/停止频繁切换时需要控制动画播放频率)
     */
    private var mLastAnimation: Long = 0
    private val mAnimHandler = Handler()

    /**
     * 添加弱引用
     */
    init {
        val mActivity = WeakReference(context)
        val mContext = mActivity.get()
        initView(mContext, attrs)
    }

    /**
     * 初始化视图滚动控件的各个滚动参数
     *
     * @param context
     * @param attrs
     */
    private fun initView(context: Context?, attrs: AttributeSet?) {
//
//        //设置为垂直方向
//        this.orientation = LinearLayout.VERTICAL
//        //抗锯齿效果
//        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        //获取自定义属性
//        val array = context!!.obtainStyledAttributes(attrs, R.styleable.JDAdverView)
//        //默认高度
//        mAdverHeight = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            jdAdverHeight,
//            getResources().getDisplayMetrics()
//        )
//        //视图滚动方向
//        mAnimDirection = array.getInteger(R.styleable.JDAdverView_animDirection, DIRECTION_UP)
//        //动画的播放时间间隔
//        val animDuration = array.getInteger(R.styleable.JDAdverView_animDuration2, mAnimDuration)
//        //视图滚动的时间间隔
//        val gap = array.getInteger(R.styleable.JDAdverView_gap, mGap)
//        //初始化视图滚动时间间隔等参数
//        if (gap >= animDuration) {
//            mGap = gap
//            mAnimDuration = animDuration
//        }
//        //关闭清空TypedArray
//        array.recycle()
    }

//    /**
//     * 用于监听ViewGroup中的View的层次变化
//     * 这个接口中包含了两个监听方法：
//     * public abstract void onChildViewAdded (View parent, View child)
//     * public abstract void onChildViewRemoved (View parent, View child)
//     *
//     * @param listener
//     */
//    fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
//        super.setOnHierarchyChangeListener(listener)
//    }
//
//    /**
//     * 设置数据适配器
//     */
//    fun setAdapter(adapter: SpNoticeViewAdapter) {
//        mAdapter = adapter
//        setupAdapter()
//    }
//
//    /**
//     * 获取Adapter
//     */
//    fun getAdapter(): SpNoticeViewAdapter? {
//        return mAdapter
//    }
//
//    /**
//     * 直接开始滚动,不设延迟
//     */
//    fun start() {
//        start(false, 0)
//    }
//
//    /**
//     * 延时启动
//     *
//     *
//     * delay: 延时启动毫秒数
//     */
//    fun start(delay: Long) {
//        start(false, delay)
//    }
//
//    /**
//     * 即时启动
//     *
//     *
//     * immediately: 是否立即启动
//     */
//    fun start(immediately: Boolean) {
//        start(immediately, 0)
//    }
//
//    /**
//     * 开启线程
//     * immediately  是否立即播放动画
//     *
//     *
//     * delay: 延时毫秒数
//     */
//    fun start(immediately: Boolean, delay: Long) {
//        if (!isStarted && null != mAdapter && mAdapter!!.getCount() > 1) {
//            isStarted = true
//            if (null == mRunnable) {
//                mRunnable = AnimRunnable(this)
//            }
//            if (immediately) {
//                //即时刷新立即播放动画
//                if (delay > 0) {
//                    post(mRunnable)
//                } else {
//                    postDelayed(mRunnable, delay)
//                }
//            } else {
//                //间隔mGap刷新一次UI
//                postDelayed(mRunnable, mGap + delay)
//            }
//        }
//    }
//
//    /**
//     * 暂时停止滚动(依然保留之前的状态)
//     *
//     *
//     * 当Activity进入后台运行后再转入前台运行，removeCallbacks无法将updateThread从message queue中移除。
//     * 以此处需要注意
//     */
//    fun pause() {
//        if (isStarted) {
//            //移除handle更新
//            removeCallbacks(mRunnable)
//            mRunnable = null
//            if (null != animatorSet) {
//                animatorSet.cancel()
//                animatorSet = null
//            }
//            isStarted = false
//            //内存回收
//            System.gc()
//        }
//    }
//
//    /**
//     * 停止并销毁视图,释放自然,避免内存泄漏
//     * 当Activity进入后台运行后再转入前台运行，removeCallbacks无法将updateThread从message queue中移除。
//     */
//    fun stopAndRelease() {
//        if (isStarted) {
//            //移除handle更新
//            removeCallbacks(mRunnable)
//            mRunnable = null
//            if (null != animatorSet) {
//                animatorSet.cancel()
//                animatorSet = null
//            }
//            isStarted = false
//            //内存回收
//            System.gc()
//        }
//        clearAllChildViews()
//        if (null != mAdapter) {
//            mAdapter.removeListener()
//            mAdapter = null
//        }
//    }
//
//    //=====================刷新视图重新开始滚动=====================//
//
//    //=====================刷新视图重新开始滚动=====================//
//    /**
//     * 延时刷新
//     */
//    fun refresh(delay: Long) {
//        //暂停原动画
//        pause()
//        //数据变化适配器需要重新刷新
//        setupAdapter()
//        //尝试启动动画
//        start(false, delay)
//    }
//
//    /**
//     * 数据更新后刷新列表
//     * 说明:
//     * 1)首先停止播放原来的动画防止出现
//     */
//    fun refresh() {
//        //暂停原动画
//        pause()
//        //数据变化适配器需要重新刷新
//        setupAdapter()
//        //尝试启动动画
//        start(false)
//    }
//
//    /**
//     * 设置数据适配
//     */
//    private fun setupAdapter() {
//        // 只有一条数据,不滚动
//        //原始Child个数
//        val childCount: Int = getChildCount()
//        if (0 == mAdapter.getCount()) {
//            // 移除所有view
//            clearAllChildViews()
//        } else if (1 == mAdapter.getCount()) {
//            // 移除所有view
//            if (0 == childCount) {
//                //原来的视图没有子视图
//                mFirstView = mAdapter.getView(this, getCacheView())
//                mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                addView(mFirstView)
//            } else if (childCount >= 1) {
//                //移除多余的子视图
//                if (childCount > 1) {
//                    for (i in childCount - 1 downTo 0) {
//                        val child: View = getChildAt(i)
//                        addCacheView(child)
//                        removeView(child)
//                    }
//                }
//                mFirstView = getChildAt(0)
//                mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//            }
//        } else {
//            // 多个数据
//            if (DIRECTION_UP == mAnimDirection) {
//                if (0 == childCount) {
//                    mFirstView = mAdapter.getView(this, getCacheView())
//                    mSecondView = mAdapter.getView(this, getCacheView())
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                    // 把2个添加到此控件里
//                    addView(mFirstView)
//                    addView(mSecondView)
//                } else if (1 == childCount) {
//                    mFirstView = getChildAt(0)
//                    mSecondView = mAdapter.getView(this, getCacheView())
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                    addView(mSecondView)
//                } else {
//                    mFirstView = getChildAt(0)
//                    mSecondView = getChildAt(1)
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                }
//            } else {
//                if (0 == childCount) {
//                    mFirstView = mAdapter.getView(this, getCacheView())
//                    mSecondView = mAdapter.getView(this, getCacheView())
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                    // 把2个添加到此控件里
//                    addView(mSecondView)
//                    addView(mFirstView)
//                } else if (1 == childCount) {
//                    mFirstView = getChildAt(0)
//                    mSecondView = mAdapter.getView(this, getCacheView())
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                    addView(mSecondView, 0)
//                } else {
//                    mFirstView = getChildAt(1)
//                    mSecondView = getChildAt(0)
//                    mAdapter.setItem(mFirstView, mAdapter.getItem(0))
//                    mAdapter.setItem(mSecondView, mAdapter.getItem(1))
//                }
//            }
//            mPosition = 1
//            isStarted = false
//        }
//    }
//
//    /**
//     * 清理全部子控件视图(以及缓存的引用)
//     */
//    private fun clearAllChildViews() {
//        // 移除所有view
//        removeAllViews()
//        //当子控件内容全部清空时需要清理原视图缓存否则会引起视图引用异常
//        cacheViews.clear()
//    }
//
//    /**
//     * 获取一个缓存的视图
//     */
//    private fun getCacheView(): View? {
//        return if (!cacheViews.isEmpty()) {
//            //从表头获取一个元素，并从队列中删除,若队列为空，返回null
//            cacheViews.poll()
//        } else null
//    }
//
//    /**
//     * 添加一个缓存视图
//     */
//    private fun addCacheView(view: View) {
//        if (!cacheViews.contains(view)) {
//            cacheViews.offer(view)
//        }
//    }
//
//    /**
//     * 设置控件预设高度
//     *
//     *
//     * height: 预设的滚动栏高度
//     */
//    fun setAdverHeight(height: Float) {
//        if (height > 0) {
//            mAdverHeight = height
//        }
//    }
//
//    /**
//     * 测量控件的宽高
//     *
//     * @param widthMeasureSpec
//     * @param heightMeasureSpec
//     */
//    protected fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
//        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
//        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
//        var childWidth = 0
//        var childHeight = 0
//        var childState = 0
//        val paddingLeft: Int = getPaddingLeft()
//        val paddingRight: Int = getPaddingRight()
//        val paddingTop: Int = getPaddingTop()
//        val paddingBottom: Int = getPaddingBottom()
//        mItemCount = if (mAdapter == null) 0 else mAdapter.getCount()
//        if (mItemCount > 0 && (widthMode == MeasureSpec.UNSPECIFIED
//                    || heightMode == MeasureSpec.UNSPECIFIED)
//        ) {
//            val child: View = getChildAt(0)
//
//            //测量子控件（子控件自动适配）
//            measureScrapChild(child, 0, widthMeasureSpec)
//            childWidth = child.measuredWidth
//            childHeight = child.measuredHeight
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                //3.0以上版本可使用getMeasuredState
//                childState = View.combineMeasuredStates(childState, child.measuredState)
//            }
//        }
//        if (widthMode == MeasureSpec.UNSPECIFIED) {
//            widthSize = paddingLeft + paddingRight + childWidth + getVerticalScrollbarWidth()
//        } else {
//            widthSize = widthSize or (childState and View.MEASURED_STATE_MASK)
//        }
//        if (heightMode == MeasureSpec.UNSPECIFIED) {
//            heightSize =
//                paddingTop + paddingBottom + childHeight + getVerticalFadingEdgeLength() * 2
//        }
//        if (mAdapter != null) {
//            if (mAdapter.getCount() > 1) {
//                if (heightSize > mAdverHeight) {
//                    mAdverHeight = heightSize.toFloat()
//                }
//                if (widthSize > mAdverWidth) {
//                    mAdverWidth = widthSize.toFloat()
//                }
//                if (mFirstView != null) {
//                    mFirstView!!.layoutParams.width = mAdverWidth.toInt()
//                    mFirstView!!.layoutParams.height = mAdverHeight.toInt()
//                }
//                if (mSecondView != null) {
//                    mSecondView!!.layoutParams.width = mAdverWidth.toInt()
//                    mSecondView!!.layoutParams.height = mAdverHeight.toInt()
//                }
//                if (DIRECTION_DOWN == mAnimDirection) {
//                    // 移动到mFirstView
//                    scrollTo(0, mAdverHeight.toInt())
//                }
//            } else {
//                // 移动到恢复到第一个子视图的位置放置出现空白(例如从向下滚动多屏变为一屏控件默认位置为(0, mAdverHeight)出现空白)
//                // 针对只有一条数据不滚动的情况
//                if (heightSize > mAdverHeight) {
//                    mAdverHeight = heightSize.toFloat()
//                }
//                if (widthSize > mAdverWidth) {
//                    mAdverWidth = widthSize.toFloat()
//                }
//                if (mFirstView != null) {
//                    mFirstView!!.layoutParams.width = mAdverWidth.toInt()
//                    mFirstView!!.layoutParams.height = mAdverHeight.toInt()
//                }
//                scrollTo(0, 0)
//            }
//        }
//
//        //设置控件的测量宽度和高度
//        setMeasuredDimension(widthSize, heightSize)
//    }
//
//    /**
//     * 测量子控件的高度
//     *
//     * @param child:            子控件
//     * @param i:                子控件索引
//     * @param widthMeasureSpec: 父控件测量宽度
//     */
//    private fun measureScrapChild(child: View, i: Int, widthMeasureSpec: Int) {
//        var p: LinearLayout.LayoutParams? = child.layoutParams as LinearLayout.LayoutParams
//        if (p == null) {
//            p = generateDefaultLayoutParams() as LinearLayout.LayoutParams?
//            child.layoutParams = p
//        }
//        val paddingLeft: Int = getPaddingLeft()
//        val paddingRight: Int = getPaddingRight()
//        val childWidthSpec = ViewGroup.getChildMeasureSpec(
//            widthMeasureSpec,
//            paddingLeft + paddingRight, p!!.width
//        )
//        val lpHeight = p.height
//        val childHeightSpec: Int
//        childHeightSpec = if (lpHeight > 0) {
//            MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY)
//        } else {
//            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//        }
//        child.measure(childWidthSpec, childHeightSpec)
//    }
//
//    /**
//     * 画布局
//     *
//     * @param canvas
//     */
//    protected fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//    }
//
//    /**
//     * 垂直滚动
//     */
//    private fun performSwitch() {
//        if (mAdapter.getCount() <= 1) {
//            return
//        }
//        if (null == animatorSet) {
//            val offsetY = if (DIRECTION_UP == mAnimDirection) -mAdverHeight else mAdverHeight
//            val animator1: ObjectAnimator = ObjectAnimator.ofFloat(
//                mFirstView,
//                "translationY",
//                ViewHelper.getTranslationY(mFirstView) + offsetY
//            )
//            val animator2: ObjectAnimator = ObjectAnimator.ofFloat(
//                mSecondView,
//                "translationY",
//                ViewHelper.getTranslationY(mSecondView) + offsetY
//            )
//            //动画集
//            animatorSet = AnimatorSet()
//            animatorSet.playTogether(animator1, animator2) //2个动画一起
//            animatorSet.addListener(object : AnimatorListenerAdapter() {
//                fun onAnimationStart(animation: Animator?) {
//                    super.onAnimationStart(animation)
//                }
//
//                fun onAnimationEnd(animation: Animator?) { //动画结束
//                    if (null != mFirstView) {
//                        ViewHelper.setTranslationY(mFirstView, 0)
//                    }
//                    if (null != mSecondView) {
//                        ViewHelper.setTranslationY(mSecondView, 0)
//                    }
//                    val lastChildIndex = if (DIRECTION_UP == mAnimDirection) 0 else 1
//                    val removedView: View = getChildAt(lastChildIndex) //获得第一个子布局
//                    if (null != removedView) {
//                        mPosition++
//                        //设置显示的布局
//                        mAdapter.setItem(
//                            removedView,
//                            mAdapter.getItem(mPosition % mAdapter.getCount())
//                        )
//                        //移除前一个view
//                        removeView(removedView)
//                        //添加下一个view
//                        val secondChildIndex = if (DIRECTION_UP == mAnimDirection) 1 else 0
//                        addView(removedView, secondChildIndex)
//                    }
//                }
//            })
//            animatorSet.setDuration(mAnimDuration)
//        }
//        val curTimed = System.currentTimeMillis()
//        if (curTimed - mLastAnimation >= mGap) {
//            //控制刷新频度(如果与上一次动画播放间距小于动画播放间距不进行播放)
//            mLastAnimation = curTimed
//            animatorSet.start()
//        }
//    }
//
//    /**
//     * 判断动画滚动方向
//     */
//    fun isAnimationUp(): Boolean {
//        return mAnimDirection > 0
//    }
//
//
//    /**
//     * 循环滚动线程
//     */
//    internal class AnimRunnable(noticeView: SpNoticeView) :
//        Runnable {
//        private val reference: WeakReference<SpNoticeView> = WeakReference<SpNoticeView>(noticeView)
//        override fun run() {
//            val mNoticeView: SpNoticeView? = reference.get()
//            if (mNoticeView?.mRunnable != null) {
//                mNoticeView.performSwitch()
//                mNoticeView.postDelayed(mNoticeView.mRunnable, mNoticeView.mGap.toLong())
//            }
//        }
//    }
//
//
//    /**
//     * 屏幕 旋转
//     *
//     * @param newConfig
//     */
//    override fun onConfigurationChanged(newConfig: Configuration?) {
//        super.onConfigurationChanged(newConfig)
//    }
//
//    /**
//     * 设置滚动的事件间隔
//     *
//     * @param gap
//     */
//    fun setmGap(gap: Int) {
//        if (gap <= 0) {
//            mGap = 4000
//        } else {
//            mGap = gap
//        }
//    }
//
//    /**
//     * 设置滚动的方向
//     *
//     * @param mAnimDirection
//     */
//    fun setmAnimDirection(mAnimDirection: Int) {
//        this.mAnimDirection = mAnimDirection
//    }
//
//    fun isStarted(): Boolean {
//        return isStarted
//    }
}