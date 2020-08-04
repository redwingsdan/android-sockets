package com.vantageclient.dvrclient;

import java.util.List;

import com.vantageclient.dvrclient.CameraListActivity.CameraInfo;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class InteractiveArrayAdapter extends ArrayAdapter<CameraInfo> {

  public final List<CameraInfo> list;
  private final Activity context;

  public InteractiveArrayAdapter(Activity context, List<CameraInfo> list) {
    super(context, R.layout.dvr_camera_row_with_checkbox, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    protected TextView text;
    protected CheckBox checkbox;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    if (convertView == null) {
      LayoutInflater inflator = context.getLayoutInflater();
      view = inflator.inflate(R.layout.dvr_camera_row_with_checkbox, null);
      final ViewHolder viewHolder = new ViewHolder();
      viewHolder.text = (TextView) view.findViewById(R.id.label);
      viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
      viewHolder.checkbox
          .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            	CameraInfo element = (CameraInfo) viewHolder.checkbox
                  .getTag();
              element.setSelected(buttonView.isChecked());
              int a = 5;
              if(isChecked)
              {
            	  viewHolder.checkbox.setBackgroundColor(Color.WHITE);
              }
              else
              {
            	  
            	  viewHolder.checkbox.setBackgroundColor(Color.parseColor("#C1C1C1"));
              }

            }
          });
      view.setTag(viewHolder);
      viewHolder.checkbox.setTag(list.get(position));
    } else {
      view = convertView;
      ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
    }
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.text.setText(list.get(position).toString());
    holder.checkbox.setChecked(list.get(position).isSelected());
    return view;
  }
}