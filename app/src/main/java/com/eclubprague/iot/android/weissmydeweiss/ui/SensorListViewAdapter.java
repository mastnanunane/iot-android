package com.eclubprague.iot.android.weissmydeweiss.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eclubprague.iot.android.weissmydeweiss.R;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.Sensor;

import java.util.List;

/**
 * Created by paulos on 14. 7. 2015.
 */
public class SensorListViewAdapter extends ArrayAdapter<Sensor> {

    public SensorListViewAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SensorListViewAdapter(Context context, int resource, Sensor[] objects) {
        super(context, resource, objects);
    }

    public SensorListViewAdapter(Context context, int resource, List<Sensor> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_img_twolines, parent, false);
        }

        Sensor s = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.firstLine);
        textView.setText(s.getUuid());
        TextView textView2 = (TextView) convertView.findViewById(R.id.secondLine);
        textView2.setText(Integer.toString(s.getType()));

        return convertView;
    }
}
