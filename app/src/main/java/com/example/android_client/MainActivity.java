package com.example.android_client;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.UI.camera.CameraFragment;
import com.example.UI.map.MapFragment;
import com.example.UI.share.ShareFragment;
import com.example.adapter.CustomViewPager;
import com.example.adapter.FragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private CustomViewPager mViewPager;
    private BottomNavigationView navigation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mViewPager.setOffscreenPageLimit(1);
    }




    @Override
    protected void onResume() {
        super.onResume();
        String fragmentToLoad = getIntent().getStringExtra("FRAGMENT_TO_LOAD");
        if ("ShareFragment".equals(fragmentToLoad)) {
            mViewPager.setCurrentItem(3);
        }
    }

    public void initView() {
        // BottomNavigationView
        navigation = findViewById(R.id.nav_view);
        // 去除背景底色
        navigation.setItemIconTintList(null);
        // 实例化adapter，得到fragment
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewpager);
        // 建立连接
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setSwipeEnabled(false);//所有的fragment都不能左右滑动切换

        setNavigation();
    }

    /**
     * 设置底部导航栏
     */
    public void setNavigation() {

        // 底部导航栏点击事件
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                resetIcon();
                switchMenu(item);
                return true;
            }
        });

        //viewpager监听事件，当viewpager滑动时得到对应的fragment碎片
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CameraFragment cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 2);
                if (position == 2) {
                    if (cameraFragment != null) {
                        getSupportFragmentManager().beginTransaction().attach(cameraFragment).commit();
                    }
                } else {
                    if (cameraFragment != null) {
                        getSupportFragmentManager().beginTransaction().detach(cameraFragment).commit();
                    }
                }
                navigation.getMenu().getItem(position).setChecked(true);
                switchMenu(navigation.getMenu().getItem(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 导航栏切换方法
     */
    private void switchMenu(MenuItem item){
        switch (item.getItemId()) {
            case R.id.navigation_map:
                item.setIcon(R.drawable.map);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.navigation_message:
                mViewPager.setCurrentItem(1);
                item.setIcon(R.drawable.message);
                break;
            case R.id.navigation_camera:
                mViewPager.setCurrentItem(2);
                item.setIcon(R.drawable.camera);
                break;
            case R.id.navigation_share:
                mViewPager.setCurrentItem(3);
                item.setIcon(R.drawable.share);
                break;
            case R.id.navigation_mine:
                mViewPager.setCurrentItem(4);
                item.setIcon(R.drawable.mine);
                break;
            default:
        }
    }

//    public void setViewPagerPagingEnabled(boolean enabled) {
//        CustomViewPager viewPager = findViewById(R.id.viewpager);
//        viewPager.setSwipeEnabled(enabled);
//    }

    /**
     * 重置底部导航栏图标
     */
//    private void resetIcon() {
//        MenuItem home = navigation.getMenu().findItem(R.id.navigation_map);
//        home.setIcon(R.drawable.map);
//        home = navigation.getMenu().findItem(R.id.navigation_message);
//        home.setIcon(R.drawable.message);
//        home = navigation.getMenu().findItem(R.id.navigation_share);
//        home.setIcon(R.drawable.share);
//        home = navigation.getMenu().findItem(R.id.navigation_mine);
//        home.setIcon(R.drawable.mine);
//    }
}