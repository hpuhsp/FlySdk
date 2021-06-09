package com.chivalrous.sdk.widget.flowlayout;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/12/11 10:07
 * @UpdateRemark:
 */
public class TagView extends FrameLayout implements Checkable {
    private boolean isChecked;
    // 针对子View是TextView的时候是否需要显示Bold样式
    private boolean showBoldTxt = false;
    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, boolean isBold) {
        super(context);
        showBoldTxt = isBold;
    }


    public View getTagView() {
        return getChildAt(0);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }


    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            View tagView = getTagView();

            if (showBoldTxt) {
                if (null != tagView
                        && (tagView instanceof TextView)) {
                    TextView mTv = (TextView) tagView;
                    if (checked) {
                        mTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    } else {
                        mTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    }
                }
            }
            refreshDrawableState();
        }
    }

    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle() {
        setChecked(!isChecked);
    }


}
