package com.hqs.common.view.qstabs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hqs.common.utils.Log;
import com.hqs.common.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by super on 2017/8/11.
 */

public class QTabView extends RelativeLayout {

    private ArrayList<String> titles;
    private int selectedTitleColor = Color.RED;
    private int indicatorColor = Color.RED;
    private int selectedIndex = 0;
    private int titleColor = Color.BLACK;
    private QRecyclerView recyclerView;
    private int indicatorHeight = 10;
    private int titlePadding = 50;
    private OnClickTabListener onClickTabListener;
    private int pageWidth = 100;
    private IndicatorView indicatorView;
    private RecyclerViewAdapter adapter;

    public QTabView(Context context) {
        super(context);
        init();
    }

    public QTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        pageWidth = (int) ScreenUtils.screenW(getContext());

        this.recyclerView = new QRecyclerView(getContext());
        this.addView(recyclerView);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(layoutParams);

        this.indicatorView = new IndicatorView(getContext());
        this.addView(indicatorView);

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, indicatorHeight);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        indicatorView.setLayoutParams(layoutParams);


        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        recyclerView.setLayoutManager(layout);

        recyclerView.setOnScrolledListener(new OnScrolledListener() {
            @Override
            public void onScrolled(int sx) {
                updateIndicatorOffset(sx);
            }
        });

        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public void setSelectedTitleColor(int selectedTitleColor) {
        this.selectedTitleColor = selectedTitleColor;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public void setTitlePadding(int titlePadding) {
        this.titlePadding = titlePadding;
    }

    public void setOnClickTabListener(OnClickTabListener onClickTabListener) {
        this.onClickTabListener = onClickTabListener;
    }

    private int preLeft = 0;

    private void updateIndicatorOffset(int sx){
        indicatorView.offset = sx;
        indicatorView.invalidate();
    }

    public void updateIndicatorOffsetAndSize(int left){

        float offset = left % pageWidth;
        int index = left / pageWidth;
        if (offset == 0){
            selectedIndex = index;
            adapter.notifyDataSetChanged();
        }

        int l = 0;
        int r = 0;
        float t = offset / pageWidth;

        if (left > preLeft){
            // 左滑
            if (index >= titles.size() - 1){
                return;
            }

            l = adapter.offsets.get(index).left;
            float s1 = adapter.offsets.get(index + 1).left - l;
            float a1 = s1 * 2;
            l += 0.5 * a1 * t * t;

            int r1 = adapter.offsets.get(index).right;

            // 计算右边需要伸展的位置
            r = adapter.offsets.get(index + 1).right;
            int s = r - r1;
            float a = s * 2;
            s = (int) (a * t - 0.5 * a * t * t);
            r = r1 + s;

        }
        else{
            // 右滑
            index += 1;
            if (index < 1){
                return;
            }

            l = adapter.offsets.get(index).left;
            float s1 = l - adapter.offsets.get(index - 1).left;
            float a1 = s1 * 2;
            float t1 = 1 - t;
            l -= a1 * t1 - 0.5 * a1 * t1 * t1;


            // 计算右边需要伸展的位置
            r = adapter.offsets.get(index).right;
            float s = r - adapter.offsets.get(index - 1).right ;
            float a = s * 2;
            r -= 0.5 * a * t1 * t1;
        }

        indicatorView.left = l;
        indicatorView.right = r;
        indicatorView.invalidate();

        preLeft = left;


    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<QViewHolder>  {

        private Map<Integer, ViewSize> offsets = new HashMap<>();

        @Override
        public QViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View relativeLayout = inflater.inflate(R.layout.item_title, null);
            return new QViewHolder(relativeLayout);
        }

        @Override
        public void onBindViewHolder(final QViewHolder viewHolder, final int i) {

            viewHolder.tv.setText(titles.get(i));

            viewHolder.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    if (i == selectedIndex){
                        indicatorView.left = viewHolder.tv.getLeft() + viewHolder.rootView.getLeft();
                        indicatorView.right = viewHolder.tv.getRight() + viewHolder.rootView.getLeft();
                        indicatorView.offset = 0;
                        indicatorView.invalidate();

                        Log.print(viewHolder.tv.getLeft() + viewHolder.rootView.getLeft());
                        Log.print(viewHolder.tv.getRight() + viewHolder.rootView.getLeft());
                    }
                    ViewSize size = new ViewSize();
                    size.left = viewHolder.tv.getLeft() + viewHolder.rootView.getLeft();
                    size.right = viewHolder.tv.getRight() + viewHolder.rootView.getLeft();
                    offsets.put(i, size);

                }
            });


            if (i == selectedIndex){
                viewHolder.tv.setTextColor(selectedTitleColor);

            }
            else{
                viewHolder.tv.setTextColor(titleColor);
            }
            viewHolder.rootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedIndex = i;
                    if (onClickTabListener != null){
                        onClickTabListener.onClickTabAt(selectedIndex);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (titles == null){
                return 0;
            }
            return titles.size();
        }
    }

    private class QViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private TextView tv;

        public QViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            tv = itemView.findViewById(R.id.title);
            rootView.setPadding(titlePadding, 0, titlePadding, 0);
        }
    }

    private class IndicatorView extends View {

        private int left = 0;
        private int right = 100;
        private Paint paint;
        private int offset = 0;

        public IndicatorView(Context context) {
            super(context);
            this.paint = new Paint();
            paint.setColor(indicatorColor);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            canvas.drawRect(left - offset, 0, right - offset, indicatorHeight, paint);
        }
    }

    private class ViewSize {
        int left;
        int right;
    }

    private class QRecyclerView extends RecyclerView {

        private int sx = 0;
        private OnScrolledListener onScrolledListener;


        public QRecyclerView(Context context) {
            super(context);
        }

        @Override
        public void onScrolled(int dx, int dy) {
            super.onScrolled(dx, dy);

            sx += dx;
            if (onScrolledListener != null){
                onScrolledListener.onScrolled(sx);
            }
        }

        public void setOnScrolledListener(OnScrolledListener onScrolledListener) {
            this.onScrolledListener = onScrolledListener;
        }
    }

    private interface OnScrolledListener {
        void onScrolled(int sx);
    }

    public interface OnClickTabListener {
        void onClickTabAt(int index);
    }

}

