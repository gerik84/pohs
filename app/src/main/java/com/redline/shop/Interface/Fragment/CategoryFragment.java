package com.redline.shop.Interface.Fragment;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pavel on 19.01.2016.
 */
public class CategoryFragment extends BaseListFragment {

    @Override
    protected ListAdapter setAdapters() {
        ArrayList<HashMap<String, String>> myArrList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

// Досье на первого кота
        map = new HashMap<String, String>();
        map.put("Name", "Мурзик");
        map.put("Tel", "495 501-3545");
        myArrList.add(map);

        Log.e("LIST", "setAdapter");
//        return null;
        return new SimpleAdapter(getActivity(), myArrList, android.R.layout.simple_list_item_2,
                new String[] {"Name", "Tel"},
                new int[] {android.R.id.text1, android.R.id.text2});
//        return new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_list_item_1, demo) {

//        };

//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    }

    @Override
    protected AdapterView.OnItemClickListener setListener() {
        return null;
    }
}
