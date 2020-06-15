package cn.edu.sc.weitalk.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> arrayList;
    public ViewPagerAdapter(@NonNull FragmentManager fm, ArrayList list) {
        super(fm);
        this.arrayList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
