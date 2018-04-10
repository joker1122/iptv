package com.excellence.iptv;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sharedpreference.HistorySharedPreference;

public class SearchActivity extends AppCompatActivity {
    private TextView mTextViewSearchHistory;
    private EditText mEditText;
    private HistorySharedPreference mSharedPreferences;
    private ImageView mImageSearch;
    private ImageView mImageDelete;
    private Button mButton;
    private ArrayList<String> mArrayListHistory = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        init();
    }

    private void init() {
        Typeface regularTypeface = Typeface.createFromAsset(this.getAssets(), "Font/Roboto-Regular.ttf");
        Typeface mediumTypeface = Typeface.createFromAsset(this.getAssets(), "Font/Roboto-Medium.ttf");
        mEditText = (EditText) findViewById(R.id.et_search);
        mTextViewSearchHistory = (TextView) findViewById(R.id.tv_search_history);
        mEditText.setTypeface(regularTypeface);
        mTextViewSearchHistory.setTypeface(mediumTypeface);
        mImageSearch = (ImageView) findViewById(R.id.img_button_search);
        mImageDelete = (ImageView) findViewById(R.id.img_delete);
        mButton = (Button) findViewById(R.id.bt_cancel);
        mSharedPreferences = new HistorySharedPreference(this);
        mArrayListHistory = mSharedPreferences.readSet();
        showHistory((LinearLayout) findViewById(R.id.linearlayout_history));
        mImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mImageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String history = mEditText.getText().toString();
                mSharedPreferences.add(history);
                mArrayListHistory = mSharedPreferences.readSet();
                showHistory((LinearLayout) findViewById(R.id.linearlayout_history));
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void showHistory(LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        TextView textView = new TextView(this);
        textView.setHeight(100);
        textView.setWidth(48);
        linearLayout.addView(textView);
        for (int i = 0; i < mArrayListHistory.size(); i++) {
            String text = mArrayListHistory.get(i);
            TextView textViewHistory = new TextView(this);
            textViewHistory.setWidth(300);
            textViewHistory.setHeight(100);
            textViewHistory.setGravity(Gravity.CENTER);
            textViewHistory.setBackground(getDrawable(R.drawable.shape_history));
            textViewHistory.setText(text);
            linearLayout.addView(textViewHistory);
            TextView textViewSpace = new TextView(this);
            textViewSpace.setHeight(100);
            textViewSpace.setWidth(48);
            linearLayout.addView(textViewSpace);
        }
    }
}
