package com.profitlinechart.demo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.profitlinechart.demo.R;

/**
 * 创建人： zengming on 2017/9/18.
 * 功能：
 */

public class FragmentConstraint extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_constraint, container, false);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("第四个页面");
        return view;
    }
}
