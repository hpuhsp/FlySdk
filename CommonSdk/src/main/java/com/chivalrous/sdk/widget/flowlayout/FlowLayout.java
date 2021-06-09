package com.chivalrous.sdk.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.chivalrous.sdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/12/12 9:05
 * @UpdateRemark:
 */
public class FlowLayout extends ViewGroup {
    private static final String TAG = FlowLayout.class.getSimpleName();

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;

    protected List<List<View>> mAllViews = new ArrayList<>(); // 存储所有的View，一行一行的存储
    protected List<Integer> mLineWidth = new ArrayList<>(); // 存储每一行的宽度
    protected List<Integer> mLineHeight = new ArrayList<>(); // 存储每一行的高度

    private int mGravity;
    private List<View> lineViews = new ArrayList<>();

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mGravity = ta.getInt(R.styleable.TagFlowLayout_gravity, LEFT);
        ta.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    /**
     * 根据子View的布局文件，为子View设置测量模式和测量值；
     * 测量 = 测量模式 + 测量值；
     * 测量模式有三种：
     * 1. EXACTLY：精确值，如 50dip和match_parent
     * 2. AT_MOST: wrap_content
     * 3. UNSPECIFIED: 子控件想躲到就多大，很少见，一般Scrollviewview中自子view中用到
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec); // 获取容器的宽度
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec); // 获取宽的测量模式
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec); // 获取容器的高度
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec); // 获取高的测量模式

        // wrap_content
        int width = 0;
        int height = 0;

        // 记录每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount(); // 得到内部元素的个数

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }

            // 测量的子view的宽度和高度, 执行之后就可以通过child.getMeasuredHeight()和 child.getMeasuredWidth()获取到子view的宽高
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams(); // 得到自子view的LoayoutParams

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin; // 子view占据的宽度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin; // 子view占据的高度

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) { // 如果 行宽+当前子view的宽度 就换行
                width = Math.max(width, lineWidth); // 对比得到最大的宽度重置容器的宽度
                lineWidth = childWidth; // 重置行宽
                height += lineHeight; // 累加记录总行高为容器的高度
                lineHeight = childHeight;  // 记录行高
            } else {  // 未换行
                lineWidth += childWidth; // 不是换行，则行的宽度就等于孩子宽度累加
                lineHeight = Math.max(lineHeight, childHeight); // 得到当前行最大高度, 取当前记录的行高和当前子view行高的最大值
            }

            if (i == cCount - 1) { // 处理最后一行
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 清楚缓存，因为该方法会调用多次
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();

        int width = getWidth(); // 当前的ViewGroup的宽度

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int childRealWidth = childWidth + lp.leftMargin + lp.rightMargin; // 子view的真正占据的宽度
            int childRealHeight = childHeight + lp.bottomMargin + lp.topMargin; // 子view的真正占据的宽度

            // 换行判断，与当期控件的宽度做对比
            int childTotalWidth = lineWidth + childRealWidth;
            if (childTotalWidth > width - getPaddingLeft() - getPaddingRight()) { // 说明需要换行
                mLineHeight.add(lineHeight); // 记录当前行的高
                mAllViews.add(lineViews); // 记录当前行的Views
                mLineWidth.add(lineWidth); // 记录当前行的宽

                // 重置行数据
                lineWidth = 0;
                lineHeight = childRealHeight; // 重置行宽
                if (null != lineViews) { // 至null并且重新分配
                    lineViews = null;
                    lineViews = new ArrayList<>();
                }
            }

            lineWidth += childRealWidth;
            lineHeight = Math.max(lineHeight, childRealHeight);
            lineViews.add(child);

        }

        // 处理最后一行
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);

        // 设置子View的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            // set gravity
            int currentLineWidth = this.mLineWidth.get(i);
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    left = width - currentLineWidth + getPaddingLeft();
                    break;
            }

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                // 为子View进行布局
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc;
                if (this.mGravity == LEFT) {
                    lc = left;
                } else {
                    lc = left + lp.leftMargin;
                }

                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc); // layout为子view进行布局

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            top += lineHeight;
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
