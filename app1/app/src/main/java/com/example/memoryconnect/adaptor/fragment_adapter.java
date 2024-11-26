package com.example.memoryconnect.adaptor;


//imports
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

//this is adapter that provides correct fragment to be displayed in tab layout
public class fragment_adapter extends FragmentStateAdapter {

    //creating Array List of fragments
    private final ArrayList<Fragment> fragmentList;

    //calling constructor
    public fragment_adapter(FragmentActivity activity, ArrayList<Fragment> fragmentList) {
        super(activity);
        this.fragmentList = fragmentList;
    }

    // overriding createFragment method to return correct fragment
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    //overriding getItemCount method to return number of fragments
    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}