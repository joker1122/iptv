package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.excellence.iptv.R;
import com.tosmart.tsresolve.bean.MessageAboutProgram;

import java.util.List;

/**
 * Created by PC-001 on 2018/4/5.
 */

public class MenuAdapter extends BaseAdapter {
    private List<MessageAboutProgram> mMessageAboutProgramList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MenuAdapter(Context context, List<MessageAboutProgram> messageAboutProgramList) {
        super();
        mContext = context;
        mMessageAboutProgramList = messageAboutProgramList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMessageAboutProgramList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageAboutProgramList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if (convertView == null) {
            myHolder = new MyHolder();
            convertView = mInflater.inflate(R.layout.main_menu_item_layout, null);
            myHolder.numberView = convertView.findViewById(R.id.tv_number);
            myHolder.nextNameView = convertView.findViewById(R.id.tv_next_program_name);
            myHolder.nameView = convertView.findViewById(R.id.tv_program_name);
            myHolder.timeView = convertView.findViewById(R.id.tv_time);
            convertView.setTag(myHolder);
        } else {
            myHolder = (MyHolder) convertView.getTag();
        }
        Typeface mediumTypeface = Typeface.createFromAsset(mContext.getAssets(), "Font/Roboto-Medium.ttf");
        Typeface regularTypeface = Typeface.createFromAsset(mContext.getAssets(), "Font/Roboto-Regular.ttf");
        myHolder.numberView.setTypeface(mediumTypeface);
        myHolder.nameView.setTypeface(mediumTypeface);
        myHolder.timeView.setTypeface(regularTypeface);
        myHolder.nextNameView.setTypeface(regularTypeface);
        myHolder.timeView.setText(String.format("%s-%s", mMessageAboutProgramList.get(position).getStartTime(), mMessageAboutProgramList.get(position).getEndTime()));
        myHolder.nameView.setText(mMessageAboutProgramList.get(position).getName());
        myHolder.nextNameView.setText(mMessageAboutProgramList.get(position).getProgramName());
        myHolder.numberView.setText(mMessageAboutProgramList.get(position).getServiceId());
        return convertView;
    }

    class MyHolder {
        TextView numberView;
        TextView nameView;
        TextView timeView;
        TextView nextNameView;
    }

    private String getNumber(int id) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(id / 100);
        stringBuffer.append((id % 100) / 10);
        stringBuffer.append(id % 10);
        return new String(stringBuffer);
    }
}
