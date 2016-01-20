package com.redline.shop.Interface.Fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.redline.shop.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pavel on 19.01.2016.
 */
public class CategoryFragment extends BaseListFragment {

    @Override
    protected ListAdapter setAdapters() {
       return new MenuAdapter(getContext(), R.layout.cell_menu_category, new String[] {"Ламинат", "Паркет"});

    }

    private class MenuAdapter extends ArrayAdapter<String> {

        public MenuAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_menu_category, parent, false);

            ImageView image = (ImageView) convertView.findViewById(R.id.iv_image);
            TextView label = (TextView) convertView.findViewById(R.id.tv_label);

            if (position == 0)
                image.setImageDrawable(getResources().getDrawable(R.drawable.laminat));
            else
                image.setImageDrawable(getResources().getDrawable(R.drawable.parket));

            label.setText(getItem(position));
            return convertView;
        }
    }

    @Override
    protected AdapterView.OnItemClickListener setListener() {
        return null;
    }
}
