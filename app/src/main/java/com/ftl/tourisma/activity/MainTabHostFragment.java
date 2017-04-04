package com.ftl.tourisma.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainTabHostFragment extends Fragment implements View.OnClickListener {

    public static int lastPage = 0;
    ViewPager vpFragment;
    MainScreenPagerAdapter adapterViewPager;
    private FragmentManager fragmentManager;
    private MainActivity mainActivity;
    private ImageView ll_favorite_footer1, ll_home_footer1, ll_profile_footer1;
    private View viewHome, viewFav, viewProfile;

    public MainTabHostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_tabs, container, false);
        initViews(view);
        return view;
    }


    private void initViews(View view) {
        fragmentManager = getChildFragmentManager();
        vpFragment = (ViewPager) view.findViewById(R.id.vp_your_location);
        adapterViewPager = new MainScreenPagerAdapter(fragmentManager);
        vpFragment.setAdapter(adapterViewPager);
        vpFragment.setCurrentItem(0);
        setPagerListener();
        vpFragment.setOffscreenPageLimit(2);
    }

    @Override
    public void onClick(View v) {
/*
        switch (v.getId()) {


        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            //  mListener = (OnFragmentInteractionListener)activity;
            mainActivity = (MainActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " ");
        }
    }

    public void switchFragment(int target) {
        vpFragment.setCurrentItem(0);
    }

    private void setPagerListener() {

        vpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (Prefs.getInt(Constants.user_id, 0) == 0) {
                    if (position == 1) {
                        if (Constants.homepage.equals("homepage")) {
                            Constants.homepage = "profile";
                            vpFragment.setCurrentItem(2);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mainActivity.setActiviateIcon(position);

                if (position == 0) {
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
                    Intent intent = new Intent("TAG_REFRESH_EXPLORE");
                    localBroadcastManager.sendBroadcast(intent);
                }
                if (position == 1) {
                    if (Prefs.getInt(Constants.user_id, 0) == 0) {
                        if (Constants.homepage.equals("profile")) {
                            Constants.homepage = "homepage";
                            vpFragment.setCurrentItem(0);
                        }
                    } else {
                        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
                        Intent intent = new Intent("TAG_REFRESH_FAVOURITE");
                        localBroadcastManager.sendBroadcast(intent);
                    }
                }
                if (position == 2) {
                    if (Prefs.getInt(Constants.user_id, 0) == 0) {
                        if (Constants.homepage.equals("profile")) {
                            Constants.homepage = "homepage";
                            vpFragment.setCurrentItem(0);
                        }
                    }
                }

               /* if (position == 0) {
                    if (mainActivity.exploreNearbyFragment != null)
                        mainActivity.exploreNearbyFragment.update();
                } else if (position == 1) {
                    if (mainActivity.favouriteFragment != null)
                        mainActivity.favouriteFragment.setRefreshView(true);
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //Viewpager adapter
    public class MainScreenPagerAdapter extends FragmentStatePagerAdapter {
        //Defining the array for Tab icons..which is going to call dynamically and load it into tabBar of toolbar
        public MainScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //this method is returning the ref of our fragments
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    ExploreNearbyFragment exploreNearbyFragment = new ExploreNearbyFragment();
                    mainActivity.exploreNearbyFragment = exploreNearbyFragment;
                    return mainActivity.exploreNearbyFragment;

                case 1: // Fragment # 0 - This will show FirstFragment different title
                    FavouriteMainFragment favouriteFragment = new FavouriteMainFragment();
                    mainActivity.favouriteFragment = favouriteFragment;
                    return mainActivity.favouriteFragment;

                case 2: // Fragment # 0 - This will show FirstFragment different title
                    MyProfileFragment1 myProfileFragment = new MyProfileFragment1();
                    mainActivity.myProfileFragment = myProfileFragment;
                    return mainActivity.myProfileFragment;

                default:
                    return null;
            }
        }

        //returning the number of pages
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

}
