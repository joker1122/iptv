package adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/4/3.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> mViews;

    public ViewPagerAdapter(ArrayList<View> views) {
        super();
        mViews = views;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView(mViews.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
