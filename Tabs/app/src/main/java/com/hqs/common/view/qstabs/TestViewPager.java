package com.hqs.common.view.qstabs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hqs.common.utils.Log;

/**
 * Created by super on 2017/8/11.
 */

public class TestViewPager extends ViewPager {

    private ScrollListener scrollListener;
    private boolean isOnTouching = false;

    public TestViewPager(Context context) {
        super(context);
    }

    public TestViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (scrollListener != null){
            scrollListener.onScrollChanged(l, isOnTouching);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE){
            isOnTouching = true;
        }
        else {
            isOnTouching = false;
        }
        return super.onTouchEvent(ev);
    }

    public interface ScrollListener{
        void onScrollChanged(int left, boolean isOnTouching);
    }
}
