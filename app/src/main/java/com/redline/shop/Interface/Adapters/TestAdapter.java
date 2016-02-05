package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends ArrayAdapter<String> {

    public TestAdapter(Context context) {
        super(context, setResource(), setData());
    }

    private static int setResource(){
        return android.R.layout.simple_list_item_1;
    }

    private static List<String> setData() {
        List<String> list = new ArrayList<>();
        list.add("Test1");
        list.add("Test2");
        list.add("Test3");
        list.add("Test4");
        list.add("Test5");
        list.add("Test6");
        return list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = super.getView(position, convertView, parent);
        TextView textView = (TextView) root.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return root;
    }
}
