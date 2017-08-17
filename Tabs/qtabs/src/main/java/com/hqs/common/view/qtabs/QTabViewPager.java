package com.hqs.common.view.qtabs;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by super on 2017/8/11.
 */

public class QTabViewPager extends ViewPager {

    private ScrollListener scrollListener;

    public QTabViewPager(Context context) {
        super(context);
    }

    public QTabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (scrollListener != null){
            scrollListener.onScrollChanged(l);
        }
    }

    public interface ScrollListener{
        void onScrollChanged(int left);
    }
}
