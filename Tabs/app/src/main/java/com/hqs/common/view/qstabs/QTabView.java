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

import com.hqs.common.utils.DensityUtils;
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
    private int preLeft = 0;

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
        indicatorView.paint.setColor(indicatorColor);
    }

    public void setTitlePadding(int titlePadding) {
        this.titlePadding = titlePadding;
    }

    public void setOnClickTabListener(OnClickTabListener onClickTabListener) {
        this.onClickTabListener = onClickTabListener;
    }

    private void updateIndicatorOffset(int sx){
        indicatorView.offset = sx;
        indicatorView.invalidate();
    }

    public void updateIndicatorOffsetAndSize(int left, boolean isOnTouching){

        int d = (int) (pageWidth * 0.5);
        int time = 30;
        if (isOnTouching){
            time = 100;
        }
        int maxStep = DensityUtils.dp2px(getContext(), 8);
        float offset = left % pageWidth;
        int index = left / pageWidth;
        if (offset < d){
            adapter.selectItem(index);
        }
        else if (offset > pageWidth - d){
            adapter.selectItem(index + 1);
        }

        int l = 0;
        int r = 0;
        float t = offset / pageWidth;

        try {
            int step = 0;
            if (left > preLeft){
                // 左滑
                if (index >= titles.size() - 1){
                    return;
                }

                int l0 = adapter.offsets.get(index).left;
                int l1 = adapter.offsets.get(index + 1).left;
                float s0 = l1 - l0;
                float a0 = s0 * 2;
                l = (int) (l0 + 0.5 * a0 * t * t);

                int r0 = adapter.offsets.get(index).right;
                int r1 = adapter.offsets.get(index + 1).right;
                float s1 = r1 - r0;
                float a1 = s1 * 2;
                r = (int) (r0 + a1 * t - 0.5 * a1 * t * t);

                if (l1 > d && offset != 0){

                    int w = r1 - l1;
                    int sx = recyclerView.sx;
                    int ll = l1 - sx;
                    int s = (int) (ll + w * 0.5 - d);
                    step = (int) (s / ((1 - t) * time));

                    int ss = (int) (l1 + w * 0.5 - (sx + d));
                    if (step > ss && s > 0){
                        step = ss;
                    }
                }
            }
            else{
                // 右滑
                index += 1;
                if (index < 1){
                    return;
                }

                int l0 = adapter.offsets.get(index).left;
                int l1 = adapter.offsets.get(index - 1).left;
                float s0 = l0 - l1;
                float a0 = s0 * 2;
                float t1 = 1 - t;
                l = (int) (l0 - (a0 * t1 - 0.5 * a0 * t1 * t1));

                int r0 = adapter.offsets.get(index).right;
                int r1 = adapter.offsets.get(index - 1).right;
                float s1 = r0 - r1;
                float a1 = s1 * 2;
                r = (int) (r0 - 0.5 * a1 * t1 * t1);

                int sx = recyclerView.sx;
                int ll = l1 - sx;
                if (ll < d){

                    int w = r1 - l1;
                    int s = (int) (ll + w * 0.5 - d);
                    step = (int) (s / (t * time));

                    int ss = (int) (l1 + w * 0.5 - (sx + d));
                    if (step < ss && s < 0){
                        step = ss;
                    }
                }
            }

            if (step != 0){
                if (step > maxStep){
                    step = maxStep;
                }
                else if (step < -maxStep){
                    step = -maxStep;
                }
                Log.print(step);
                recyclerView.scrollBy(step, 0);
            }


            indicatorView.left = l;
            indicatorView.right = r;
            indicatorView.invalidate();


        } catch (Exception e) {
        }

        preLeft = left;


    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<QViewHolder>  {

        private Map<Integer, ViewSize> offsets = new HashMap<>();
        private Map<Integer, QViewHolder> viewHolders = new HashMap<>();

        @Override
        public QViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View relativeLayout = inflater.inflate(R.layout.item_title, null);
            return new QViewHolder(relativeLayout);
        }

        @Override
        public void onBindViewHolder(final QViewHolder viewHolder, final int i) {

            viewHolders.put(i, viewHolder);
            viewHolder.tv.setText(titles.get(i));

            viewHolder.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    int left = viewHolder.tv.getLeft() + viewHolder.rootView.getLeft() + recyclerView.sx;
                    int right = viewHolder.tv.getRight() + viewHolder.rootView.getLeft() + recyclerView.sx;
                    if (i == selectedIndex){
                        indicatorView.left = left;
                        indicatorView.right = right;
                        indicatorView.invalidate();
                    }
                    ViewSize size = new ViewSize();
                    size.left = left;
                    size.right = right;
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
                    deselectItem(selectedIndex);
                    selectedIndex = i;
                    if (onClickTabListener != null){
                        onClickTabListener.onClickTabAt(selectedIndex);
                    }
                }
            });
        }

        public void selectItem(int index){
            viewHolders.get(selectedIndex).tv.setTextColor(titleColor);
            viewHolders.get(index).tv.setTextColor(selectedTitleColor);
            selectedIndex = index;
        }

        public void deselectItem(int index){
            viewHolders.get(index).tv.setTextColor(titleColor);
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

