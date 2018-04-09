package com.excellence.iptv;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tosmart.tsresolve.bean.MessageAboutProgram;
import com.tosmart.tsresolve.bean.MessageAboutTs;
import com.tosmart.tsresolve.resolveTs.GetCorrectSection;
import com.tosmart.tsresolve.resolveTs.ResolveTs;
import com.tosmart.tsresolve.resolvetable.Eit;
import com.tosmart.tsresolve.resolvetable.Pat;
import com.tosmart.tsresolve.resolvetable.Pmt;
import com.tosmart.tsresolve.resolvetable.Sdt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import adapter.SelectTsFileAdapter;

public class MainActivity extends AppCompatActivity {
    public static final String path = Environment.getExternalStorageDirectory().getPath() + "/ts";
    public static final int RESULT_CODE = 1;
    public static final int FINISH = -1;
    public static final int ERROR = 0;
    private ListView mListView;
    private ArrayList<String> mArrayList;
    private SelectTsFileAdapter mSelectTsFileAdapter;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLoadingView;
    private MyHandler mMyHandler;
    private MessageAboutTs mMessageAboutTs;
    private ArrayList<MessageAboutProgram> mMessageAboutPrograms;
    private ArrayList<String> mProgramMapPid;
    private Map<String, String> mMapArrayList;
    private ImageView mImageView;
    private TextView mHead;
    private Dialog mLoadingDialog;
    private ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(4, 10, 200, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(5));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity_layout);
        init();
        requestPremission();
        mSelectTsFileAdapter = new SelectTsFileAdapter(this, mArrayList);
        mListView.setAdapter(mSelectTsFileAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog();
                mListView.setEnabled(false);
                TextView textView = (TextView) view.findViewById(R.id.item_name);
                final String filePath = new String(mMapArrayList.get(textView.getText()));
                mThreadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        doInBackground(filePath);
                    }
                });
            }
        });
    }

    private void init() {
        Typeface mediumTypeface = Typeface.createFromAsset(this.getAssets(), "Font/Roboto-Medium.ttf");
        mHead = (TextView) findViewById(R.id.tv_select_head);
        mHead.setTypeface(mediumTypeface);
        mLoadingView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loading_layout, null);
        mLoadingDialog = new Dialog(this, R.style.loading_style);
        Window dialogWindow = mLoadingDialog.getWindow();
        mLayoutParams = dialogWindow.getAttributes();
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = getResources().getDimensionPixelSize(R.dimen.loading_out_width);
        mLayoutParams.height = getResources().getDimensionPixelSize(R.dimen.loading_out_height);
        mLoadingDialog.setContentView(mLoadingView);
        mImageView = (ImageView) mLoadingView.findViewById(R.id.iv_load_image);
        mMapArrayList = new HashMap<>();
        mArrayList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.lv_file_name);
        mMyHandler = new MyHandler();
    }

    private void getTS(String mpath) {
        int length = mpath.length() + 1;
        File file = new File(mpath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File value : files) {
                if (value.isDirectory()) {
                    getTS(value.getPath());
                } else {
                    mMapArrayList.put(value.getPath().substring(length), value.getPath());
                    mArrayList.add(value.getPath().substring(length));
                }

            }
        }
    }

    private void requestPremission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_CODE);
            }
        } else {
            getTS(path);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RESULT_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getTS(path);
                mSelectTsFileAdapter.notifyDataSetChanged();
            }
        }
    }

    private void doInBackground(String filePath) {
        mMessageAboutTs = ResolveTs.resolveFile(filePath);
        if (mMessageAboutTs.getPacketLength() == -1) {
            Message message = Message.obtain();
            message.what = ERROR;
            mMyHandler.sendMessage(message);
            return;
        }
        ArrayList<byte[]> sectionList = GetCorrectSection.getSpeciallySection(0, 0, 0, mMessageAboutTs, null);
        mProgramMapPid = Pat.resolvePat(sectionList);
        mMessageAboutPrograms = Pmt.resolveAll(mProgramMapPid, mMessageAboutTs);
        Sdt.resolve(mMessageAboutPrograms, mMessageAboutTs);
        Eit.solve(mMessageAboutTs, mMessageAboutPrograms);
        Message message = Message.obtain();
        message.what = FINISH;
        mMyHandler.sendMessage(message);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH:
                    Intent intent = new Intent(MainActivity.this, ViewPageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("message", mMessageAboutPrograms);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                    mImageView.clearAnimation();
                    mLoadingDialog.hide();
                    mListView.setEnabled(true);
                    break;
                case ERROR:
                    mImageView.clearAnimation();
                    mLoadingDialog.hide();
                    mListView.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void showDialog() {
        mLoadingDialog.show();
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.load);
        LinearInterpolator interpolator = new LinearInterpolator();
        animation.setInterpolator(interpolator);
        mImageView.startAnimation(animation);

    }
}
