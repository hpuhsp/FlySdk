package com.chivalrous.sdk.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.chivalrous.sdk.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/12/11 10:07
 * @UpdateRemark:
 */
public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {

    private static final String TAG = TagFlowLayout.class.getSimpleName();

    private TagAdapter mTagAdapter;
    private boolean mAutoSelectEffect = true;
    private int mSelectedMax = -1; // -1为不限制数量
    private boolean textBold = false;
    private MotionEvent mMotionEvent;

    private Set<Integer> mSelectedView = new HashSet<>();

    private int mSpaceL = 5;
    private int mSpaceT = 5;
    private int mSpaceR = 5;
    private int mSpaceB = 5;

    private boolean isLeftAV = false;


    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mAutoSelectEffect = ta.getBoolean(R.styleable.TagFlowLayout_auto_select_effect, true);
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1);
        textBold = ta.getBoolean(R.styleable.TagFlowLayout_text_bold, false);
        ta.recycle();

        if (mAutoSelectEffect) {
            setClickable(true);
        }
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() == View.GONE) continue;
            if (tagView.getTagView().getVisibility() == View.GONE) {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setSpace(int space) {
        int tmp = dip2px(getContext(), space);
        mSpaceT = tmp;
        mSpaceB = tmp;
        mSpaceL = tmp;
        mSpaceR = tmp;
    }

    public void setSpace(int left, int top, int bottom, int right) {
        mSpaceL = dip2px(getContext(), left);
        mSpaceT = dip2px(getContext(), top);
        mSpaceB = dip2px(getContext(), bottom);
        mSpaceR = dip2px(getContext(), right);
    }

    public boolean isLeftAV() {
        return isLeftAV;
    }

    public void setLeftAV(boolean leftAV) {
        isLeftAV = leftAV;
    }

    public interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet, int targetPosition);
    }

    private OnSelectListener mOnSelectListener;

    /**
     * 监听标签选中属性
     *
     * @param onSelectListener
     */
    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
        if (mOnSelectListener != null) setClickable(true);
    }

    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }

    private OnTagClickListener mOnTagClickListener;

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
        if (onTagClickListener != null) setClickable(true);
    }


    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mSelectedView.clear();
        changeAdapter();

    }

    /**
     * 改变adapter
     */
    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = mTagAdapter;
        TagView tagViewContainer = null;
        HashSet preCheckedList = mTagAdapter.getPreCheckedList();
        for (int i = 0; i < adapter.getCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));
            tagViewContainer = new TagView(getContext(), textBold);
            tagView.setDuplicateParentStateEnabled(true);
            LayoutParams layoutParams = tagView.getLayoutParams();
            MarginLayoutParams marginParams = null;
            if (layoutParams != null) {
                //获取view的margin设置参数
                if (layoutParams instanceof MarginLayoutParams) {
                    marginParams = (MarginLayoutParams) layoutParams;
                } else {
                    //不存在时创建一个新的参数
                    marginParams = new MarginLayoutParams(layoutParams);
                }
                marginParams.setMargins(mSpaceL, mSpaceT, mSpaceR, mSpaceB);
                tagViewContainer.setLayoutParams(marginParams);

                // Android大部分机型中frameLayout可能会忽略子view的margin值。但是部分机型上不会，例如华为,去掉子view的margin
                tagView.setLayoutParams(new FrameLayout.LayoutParams(tagView.getLayoutParams().width, tagView.getLayoutParams().height));
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.setMargins(mSpaceL, mSpaceT, mSpaceR, mSpaceB);
                tagViewContainer.setLayoutParams(lp);
            }
            tagViewContainer.addView(tagView);
            if (preCheckedList.contains(i)) {
                tagViewContainer.setChecked(true);
            }
            addView(tagViewContainer);
        }
        mSelectedView.addAll(preCheckedList);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mMotionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if (mMotionEvent == null) {
            return super.performClick();
        }

        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;

        TagView child = findChild(x, y);
        int pos = findPosByView(child);
        if (child != null) {
            doSelect(child, pos);
            if (mOnTagClickListener != null) {
                return mOnTagClickListener.onTagClick(child.getTagView(), pos, this);
            }
        }
        return true;
    }

    /**
     * 设置最大选择个数
     *
     * @param count
     */
    public void setMaxSelectCount(int count) {
        if (mSelectedView.size() > count) {
            mSelectedView.clear();
        }
        mSelectedMax = count;
    }

    /**
     * 获取的选中的位置
     *
     * @return
     */
    public Set<Integer> getSelectedList() {
        return new HashSet<>(mSelectedView);
    }

    /**
     * 进行选中操作
     *
     * @param child
     * @param position
     */
    private void doSelect(TagView child, int position) {
        if (mAutoSelectEffect) {
            Iterator<Integer> iterator = mSelectedView.iterator();
            Integer preIndex = iterator.next();
            if (!child.isChecked()) {
                //处理max_select = 1的情况(单选情况下)
                if (mSelectedMax == 1 && mSelectedView.size() == 1) {
                    TagView pre = (TagView) getChildAt(preIndex);
                    pre.setChecked(false);
                    child.setChecked(true);
                    mSelectedView.remove(preIndex);
                } else {
                    if (mSelectedMax > 0 && mSelectedView.size() >= mSelectedMax)
                        return;
                    child.setChecked(true);
                }
                mSelectedView.add(position);
            } else {
                if (mSelectedMax == 1 && mSelectedView.size() == 1 && (preIndex == position)) {
                    return;
                }
                child.setChecked(false);
                mSelectedView.remove(position);
            }
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelected(new HashSet<>(mSelectedView), position);
            }
        }
    }

    public TagAdapter getAdapter() {
        return mTagAdapter;
    }

    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        String selectPos = "";
        if (mSelectedView.size() > 0) {
            for (int key : mSelectedView) {
                selectPos += key + "|";
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos)) {
                String[] split = mSelectPos.split("\\|");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    mSelectedView.add(index);

                    TagView tagView = (TagView) getChildAt(index);
                    if (tagView != null) {
                        tagView.setChecked(true);
                    }
                }

            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private int findPosByView(View child) {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View v = getChildAt(i);
            if (v == child) {
                return i;
            }
        }
        return -1;
    }

    private TagView findChild(int x, int y) {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) {
                continue;
            }
            Rect outRect = new Rect();
            v.getHitRect(outRect);
            if (outRect.contains(x, y)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void onChanged() {
        mSelectedView.clear();
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
