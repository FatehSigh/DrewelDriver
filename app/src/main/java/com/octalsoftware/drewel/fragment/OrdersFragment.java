package com.octalsoftware.drewel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 4/11/2018.
 */

public class OrdersFragment extends Fragment {

    private TabViewAdapter adapter;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);
        setupViewPager(viewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            adapter = new TabViewAdapter(this.getChildFragmentManager());
            viewPager.setAdapter(adapter);
            tabLayout.addTab(tabLayout.newTab().setText(R.string.New));
            tabLayout.addTab(tabLayout.newTab().setText(R.string.out_for_delivery));
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

//            mFragmentTitleList.clear();
//            mFragmentList.clear();
//            adapter = new TabViewAdapter(this.getChildFragmentManager());
//            int count = adapter.getCount();
//            adapter.addFragment(new NewOrdersFragment(), "NEW");
//            adapter.addFragment(new AcceptedOrdersFragment(), "NEW");
//            adapter.addFragment(new OutOfDeliveryOrdersFragment(), "OUT FOR DELIVERY");
//            viewPager.setAdapter(adapter);
//            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    switch (position) {
//                        case 0:
//                            break;
//                        case 1:
//                            break;
//                    }
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static OrdersFragment newInstance() {
        Bundle args = new Bundle();
        OrdersFragment fragment = new OrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }


    class TabViewAdapter extends FragmentStatePagerAdapter {


        TabViewAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return new AcceptedOrdersFragment();
                }
                case 1: {
                    return new OutOfDeliveryOrdersFragment();
                }
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


    }


}
