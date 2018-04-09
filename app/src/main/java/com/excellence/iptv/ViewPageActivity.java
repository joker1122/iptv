package com.excellence.iptv;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tosmart.tsresolve.bean.MessageAboutProgram;

import java.util.ArrayList;

import adapter.MenuAdapter;
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
    private ListView mListView;
    private MenuAdapter mMenuAdapter;
    private ArrayList<MessageAboutProgram> mMessageAboutPrograms;
    private TextView mTextViewLive;
    private TextView mTextViewAbout;
    private TextView mTextViewAboutHead;
    private TextView mTextViewAboutApp;
    private TextView mTextViewVersion;
    private TextView mTextViewCheckUpdate;
    private EditText mEditTextSearch;
    private ImageView mImageFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_page_layout);
        init();
    }

    private void init() {
        Typeface regularTypeface = Typeface.createFromAsset(this.getAssets(), "Font/Roboto-Regular.ttf");
        Typeface mediumTypeface = Typeface.createFromAsset(this.getAssets(), "Font/Roboto-Medium.ttf");
        mTextViewLive = (TextView) findViewById(R.id.tv_live);
        mTextViewAbout = (TextView) findViewById(R.id.tv_at);
        mTextViewAbout.setTypeface(regularTypeface);
        mTextViewLive.setTypeface(regularTypeface);
        mViewPager = (ViewPager) findViewById(R.id.view_page);
        mViewArrayList = new ArrayList<View>();
        mMenuView = getLayoutInflater().inflate(R.layout.main_menu_fragment_layout, null);
        mEditTextSearch = (EditText) mMenuView.findViewById(R.id.edit_search);
        mImageFavorite = (ImageView) mMenuView.findViewById(R.id.icon_fav);
        mEditTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPageActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        mImageFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPageActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });
        mEditTextSearch.setTypeface(regularTypeface);
        mAboutView = getLayoutInflater().inflate(R.layout.about_fragment_layout, null);
        mTextViewAboutHead = mAboutView.findViewById(R.id.tv_about);
        mTextViewAboutApp = mAboutView.findViewById(R.id.tv_about_app);
        mTextViewVersion = mAboutView.findViewById(R.id.tv_version_message);
        mTextViewCheckUpdate = mAboutView.findViewById(R.id.tv_check_update);
        mTextViewAboutHead.setTypeface(mediumTypeface);
        mTextViewAboutApp.setTypeface(mediumTypeface);
        mTextViewCheckUpdate.setTypeface(mediumTypeface);
        mTextViewVersion.setTypeface(regularTypeface);
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
        mListView = (ListView) mMenuView.findViewById(R.id.lv_message_about_item);
        mMessageAboutPrograms = getIntent().getBundleExtra("bundle").getParcelableArrayList("message");
        mMenuAdapter = new MenuAdapter(ViewPageActivity.this, mMessageAboutPrograms);
        mListView.setAdapter(mMenuAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewPageActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(mListView);
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.favorite_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this, "kk", Toast.LENGTH_SHORT).show();
        return true;
    }
}
