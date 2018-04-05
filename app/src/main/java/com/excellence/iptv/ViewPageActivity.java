package com.excellence.iptv;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import adapter.ViewPagerAdapter;

public class ViewPageActivity extends AppCompatActivity {
    private static final int MENU_VIEW_INDEX = 0;
    private static final int ABOUT_VIEW_INDEX = 1;
    private ViewPager mViewPager;
    private ArrayList<View> mViewArrayList;
    private ViewPagerAdapter mViewPagerAdapter;
    private View mMenuView;
    private View mAboutView;
    private ImageView mTvImageView;
    private ImageView mAboutImageView;
    private ViewPager.SimpleOnPageChangeListener mSimpleOnPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_page_layout);
        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.view_page);
        mViewArrayList = new ArrayList<View>();
        mMenuView = getLayoutInflater().inflate(R.layout.main_menu_fragment, null);
        mAboutView = getLayoutInflater().inflate(R.layout.about_fragment, null);
        mTvImageView = (ImageView) findViewById(R.id.iv_tv);
        mAboutImageView = (ImageView) findViewById(R.id.iv_about);
        mTvImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mAboutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        mViewArrayList.add(mMenuView);
        mViewArrayList.add(mAboutView);
        mViewPagerAdapter = new ViewPagerAdapter(mViewArrayList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mSimpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case MENU_VIEW_INDEX:
                        mTvImageView.setImageDrawable(getDrawable(R.drawable.bottom_icon_live_light));
                        mAboutImageView.setImageDrawable(getDrawable(R.drawable.bottom_icon_about_normal));
                        break;
                    case ABOUT_VIEW_INDEX:
                        mTvImageView.setImageDrawable(getDrawable(R.drawable.bottom_icon_live_normal));
                        mAboutImageView.setImageDrawable(getDrawable(R.drawable.bottom_icon_about_light));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };
        mViewPager.addOnPageChangeListener(mSimpleOnPageChangeListener);
    }
}
