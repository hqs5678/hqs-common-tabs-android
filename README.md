# QTabView

Android 标签控件， 效果与半糖主页的标签也类似。

##### 运行效果图
![运行效果图](https://github.com/hqs5678/hqs-common-tabs/blob/master/2017-08-16%2015_48_16.gif)

### 功能实现
- 支持横竖屏切换
- 自定义标题样式
- 自定义标题间距
- 设置标题下indicator颜色、高度，可以和选中的标题颜色不一致

### 安装说明
### Gradle
```
    compile 'com.hqs.common.view.qtabs:qtabs:1.0.2'
```

 
#### 使用方法
使用QTabViewPager和QTabView 配合使用， 可以实现类似新闻标签页等类似需求的界面。具体代码可以参考如下：

```
tabView = (QTabView) findViewById(R.id.tabView);
titles.add("热点");
titles.add("新闻");
// ...
 
// 设置样式

// 设置标题
tabView.setTitles(titles);

// 设置选中标题的颜色
tabView.setSelectedTitleColor(Color.GRAY);

// 设置标题小面的横断线的颜色
tabView.setIndicatorColor(Color.LTGRAY);

// 设置普通标题的颜色
tabView.setTitleColor(Color.LTGRAY);
 
// 设置标题的字体大小
tabView.setTitleFontSize(16);
  
// 设置点击标签的事件监听器（必须）
tabView.setOnClickTabListener(new QTabView.OnClickTabListener() {
    @Override
    public void onClickTabAt(int index) {
        viewPager.setCurrentItem(index);
    }
});
// 为tabView 设置viewPager（必须）
tabView.setViewPager(viewPager);
```

#### 以下为简单 Demo，仅为参考。
1. activity_main.xml， 简单的demo，您可以根据个人需求编写UI。

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >
 
    <com.hqs.common.view.qtabs.QTabView
        android:id="@+id/tabView"
        android:layout_width="match_parent"
        android:layout_height="60dp" />

    <com.hqs.common.view.qtabs.QTabViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" /> 

</LinearLayout>


```

2. MainActivity.java 关键代码

```
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    ActivityUtil.hideActionBar(this);

    viewPager = (QTabViewPager) findViewById(R.id.viewPager);
    tabView = (QTabView) findViewById(R.id.tabView);

    titles.add("热点");
    titles.add("新闻");
    titles.add("美文");
    titles.add("时尚潮流");
    titles.add("学生党");
    titles.add("美女");
    titles.add("美男");
    titles.add("夕阳红");
    titles.add("少年派");
    titles.add("大学生");
    titles.add("孩子");
    titles.add("两性");
     
    // 设置标题
    tabView.setTitles(titles);
    
    // 设置选中标题的颜色
    tabView.setSelectedTitleColor(Color.GRAY);
    
    // 设置标题小面的横断线的颜色
    tabView.setIndicatorColor(Color.LTGRAY);
    
    // 设置普通标题的颜色
    tabView.setTitleColor(Color.LTGRAY);
    
    // 设置标题的字体大小
    tabView.setTitleFontSize(16);


    // 以fragment + viewPager 为例
    // 为考虑性能问题，仅作为Demo示例，您可以根据个人需求编写核心代码
    fragments = new ArrayList<>();
    for (int i = 0; i < titles.size(); i++){
        MyFragment fragment = new MyFragment();
        fragment.title = titles.get(i) + "-" + i;
        if (i % 2 == 0){
            fragment.setBgColor(Color.LTGRAY);
        }
        else{
            fragment.setBgColor(Color.argb(23, 23, 23, 23));
        }
        fragments.add(fragment);
    }

    // adapter
    TestFragmentPagerAdapter adapter = new TestFragmentPagerAdapter(getSupportFragmentManager());
    adapter.fragments = fragments;
    viewPager.setAdapter(adapter);


    // 设置点击标签的事件监听器（必须）
    tabView.setOnClickTabListener(new QTabView.OnClickTabListener() {
        @Override
        public void onClickTabAt(int index) {
            viewPager.setCurrentItem(index);
        }
    });
    // 为tabView 设置viewPager（必须）
    tabView.setViewPager(viewPager);

}

```

3. TestFragmentPagerAdapter 核心代码

```
public class TestFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<MyFragment> fragments;

    public TestFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
```

4. MyFragment 核心代码

```
public static class MyFragment extends Fragment {

    private String title = "";
    private int bgColor = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // fragment 支持横竖屏切换
        // 本示例中必须（fragment的特殊的生命周期所致）
        setRetainInstance(true);
        
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        TextView tv = new TextView(getContext());
        tv.setText(this.title);
        tv.setTextSize(DensityUtils.dp2px(getContext(), 20));
        relativeLayout.addView(tv);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
        params.addRule(relativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        tv.setLayoutParams(params);

        relativeLayout.setBackgroundColor(bgColor);
        return relativeLayout;
    }


    public void setBgColor(int color){
        this.bgColor = color;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
```

如有其他问题， 请下载运行项目。
