//package com.ftl.tourisma.adapters;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import com.ftl.tourisma.fragments.PageFragment;
//import com.viewpagerindicator.IconPagerAdapter;
//
//import java.util.ArrayList;
//
///**
// * Created by c162 on 20/01/17.
// */
//
//public class CustomPagerAdapter  extends FragmentPagerAdapter implements
//        IconPagerAdapter {
//
//    private ArrayList listIcon;
//    private ArrayList pageContents;
//
//    public CustomPagerAdapter(FragmentManager fm, ArrayList list,
//                              ArrayList content) {
//        super(fm);
//        this.listIcon = list;
//        this.pageContents = content;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//
//        return PageFragment.getInstance(pageContents.get(position), position);
//    }
//
//    @Override
//    public int getCount() {
//        return 4;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (position == 0) {
//            return "Music";
//        } else if (position == 1) {
//            return "Video";
//        } else if (position == 2) {
//            return "Food";
//        } else
//            return "Friend";
//    }
//
//    @Override
//    public int getIconResId(int index) {
//        return listIcon.get(index);
//    }
//
//}