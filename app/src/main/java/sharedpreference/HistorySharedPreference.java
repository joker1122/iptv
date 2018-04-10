package sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by PC-001 on 2018/4/9.
 */

public class HistorySharedPreference {
    public static final int MAX_SIZE = 3;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Set<String> mStringSet = new HashSet<>();

    public HistorySharedPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences("history", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mStringSet = mSharedPreferences.getStringSet("value", new HashSet<String>());
    }

    public void add(String history) {
        if (mStringSet.size() == MAX_SIZE) {
            deleteFirst();
        }
        mStringSet.add(history);
        mEditor.putStringSet("value", mStringSet);
    }

    public void delete(String value) {
        mStringSet.remove(value);
        mEditor.putStringSet("value", mStringSet);
    }

    public ArrayList<String> readSet() {
        ArrayList<String> list = new ArrayList<>();
        Iterator<String> iterator = mStringSet.iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    private void deleteFirst() {
        Iterator<String> iterator = mStringSet.iterator();
        mStringSet.clear();
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                mStringSet.add(iterator.next());
            }
        }
    }
}
