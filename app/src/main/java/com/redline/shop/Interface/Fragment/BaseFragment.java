package com.redline.shop.Interface.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pavel on 19.01.2016.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract int setResourceId();

    protected abstract void initView(View root);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(setResourceId(), container, false);
        initView(root);
        return root;
    }
}
