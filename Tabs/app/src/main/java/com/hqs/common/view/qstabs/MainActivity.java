package com.hqs.common.view.qstabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hqs.common.utils.ColorUtil;
import com.hqs.common.utils.DensityUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private QTabView tabView;

    private ArrayList<MyFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabView = (QTabView) findViewById(R.id.tabView);

        fragments = new ArrayList<>();
        for (int i = 0; i < 20; i++){
            MyFragment fragment = new MyFragment();
            fragment.setPosition(i);
            fragments.add(fragment);
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }


    public static class MyFragment extends Fragment {

        private int position = 0;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            TextView tv = new TextView(getContext());
            tv.setText(position + "");
            tv.setTextSize(DensityUtils.dp2px(getContext(), 30));
            relativeLayout.addView(tv);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(relativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            tv.setLayoutParams(params);

            relativeLayout.setBackgroundColor(ColorUtil.randomColor());
            return relativeLayout;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
