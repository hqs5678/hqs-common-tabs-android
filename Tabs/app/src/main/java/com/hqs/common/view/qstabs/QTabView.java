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
    private RecyclerView recyclerView;
    private int indicatorHeight = 10;
    private int titlePadding = 50;

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
        this.recyclerView = new RecyclerView(getContext());
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

    private int preLeft = 0;

    public void updateIndicatorOffset(int left, int pageWidth){

        float offset = left % pageWidth;
        if (offset == 0){
            selectedIndex = left / pageWidth;
            adapter.notifyDataSetChanged();
        }
        int l = 0;
        int r = 0;
        float t = offset / pageWidth;

        if (left > preLeft){
            // 右滑
            if (selectedIndex > titles.size() - 1){
                return;
            }

            l = adapter.offsets.get(selectedIndex).left;
            float s1 = adapter.offsets.get(selectedIndex + 1).left - l;
            float a1 = s1 * 2;
            l += 0.5 * a1 * t * t;

            int r1 = adapter.offsets.get(selectedIndex).right;

            // 计算右边需要伸展的位置
            r = adapter.offsets.get(selectedIndex + 1).right;
            int s = r - r1;
            float a = s * 2;
            s = (int) (a * t - 0.5 * a * t * t);
            r = r1 + s;

        }
        else{
            // 左滑
            if (selectedIndex < 1){
                return;
            }

            l = adapter.offsets.get(selectedIndex).left;
            float s1 = l - adapter.offsets.get(selectedIndex - 1).left;
            float a1 = s1 * 2;
            float t1 = 1 - t;
            l -= a1 * t1 - 0.5 * a1 * t1 * t1;


            // 计算右边需要伸展的位置
            r = adapter.offsets.get(selectedIndex).right;
            float s = r - adapter.offsets.get(selectedIndex - 1).right ;
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
                        indicatorView.right = viewHolder.tv.getWidth() + indicatorView.left;
                        indicatorView.invalidate();
                    }
                    ViewSize size = new ViewSize();
                    size.left = viewHolder.tv.getLeft() + viewHolder.rootView.getLeft();
                    size.width = viewHolder.tv.getWidth();
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
                    notifyDataSetChanged();
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

        public IndicatorView(Context context) {
            super(context);
            this.paint = new Paint();
            paint.setColor(indicatorColor);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);

            canvas.drawRect(left, 0, right, indicatorHeight, paint);
        }
    }

    private class ViewSize {
        int left;
        int right;
        int width;
    }

}

