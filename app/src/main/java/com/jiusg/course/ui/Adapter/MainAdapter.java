package com.jiusg.course.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiusg.course.R;

/**
 * Created by Administrator on 2016/5/10.
 */
public class MainAdapter extends BaseAdapter {

    private String[][] strings;
    private Context context;

    public MainAdapter(Context context, String[][] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return 36;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_gridview_main, parent, false);

        TextView textView = (TextView) convertView.findViewById(R.id.content);

        if (position == 0) {
            textView.setText(strings[0][0] + "");
        } else {
            textView.setText(strings[position / 6][position % 6] + "");
        }

        if (position < 6)
            textView.setMinHeight(50);
        else
            textView.setMinHeight(250);

        return convertView;
    }
}
