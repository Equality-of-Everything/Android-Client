package com.example.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.UI.map.MapFragment;
import com.example.UI.msg.MessageFragment;
import com.example.UI.mine.MineFragment;
import com.example.UI.share.ShareFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    // 私有成员mFragments，加载页面碎片
    private List<Fragment> mFragments = new ArrayList<>();

    public FragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        // 加载初始化Fragment
        mFragments.add(new MapFragment());
        mFragments.add(new MessageFragment());
        mFragments.add(new ShareFragment());
        mFragments.add(new MineFragment());
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mFragments.get(0);
                break;
            case 1:
                fragment = mFragments.get(1);

                break;
            case 2:
                fragment = mFragments.get(2);

                break;
            case 3:
                fragment = mFragments.get(3);
                break;
            default:
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
