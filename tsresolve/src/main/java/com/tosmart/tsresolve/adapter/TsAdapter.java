package com.tosmart.tsresolve.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tosmart.tsresolve.R;

import java.util.List;


/**
 * Created by PC-001 on 2018/3/14.
 */

public class TsAdapter extends BaseAdapter {

    private List<String> mNameList;
    private LayoutInflater mInflater;

    public TsAdapter(Context context, List<String> list) {
        super();
        mNameList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_item_layout, null);
            viewHolder.textView = convertView.findViewById(R.id.tv_ts_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mNameList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
