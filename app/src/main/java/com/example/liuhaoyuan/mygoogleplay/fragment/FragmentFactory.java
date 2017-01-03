package com.example.liuhaoyuan.mygoogleplay.fragment;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by liuhaoyuan on 2016/12/27.
 */

public class FragmentFactory {
    private static SparseArray<BaseFragment> mBaseFragmentSparseArray = new SparseArray<>();
    public static BaseFragment createFragment(int pos) {
        BaseFragment baseFragment = mBaseFragmentSparseArray.get(pos);
        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new HomeFragment();
                    break;
                case 1:
                    baseFragment = new AppFragment();
                    break;
                case 2:
                    baseFragment = new GameFragment();
                    break;
                case 3:
                    baseFragment = new SubjectFragment();
                    break;
                case 4:
                    baseFragment = new RecommendFragment();
                    break;
                case 5:
                    baseFragment = new CategoryFragment();
                    break;
                case 6:
                    baseFragment = new HotFragment();
                    break;
            }
            mBaseFragmentSparseArray.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
