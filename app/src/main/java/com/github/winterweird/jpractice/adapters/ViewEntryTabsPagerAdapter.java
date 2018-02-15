package com.github.winterweird.jpractice.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.fragments.ViewEntryPageFragmentOverview;
import com.github.winterweird.jpractice.fragments.ViewEntryPageFragmentJisho;

public class ViewEntryTabsPagerAdapter extends FragmentPagerAdapter {
    private String kanji;
    private int listname;
    private Context context;
    
    public ViewEntryTabsPagerAdapter(Context context, FragmentManager fm,
            int listname, String kanji) {
        super(fm);
        this.context = context;
        this.kanji = kanji;
        this.listname = listname;
    }
    
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ViewEntryPageFragmentOverview(listname, kanji);
            case 1:
                return new ViewEntryPageFragmentJisho(kanji);
            default:
                throw new IllegalArgumentException("view entry fragment position is not 0 or 1");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.viewEntryPageTabOverview);
            case 1:
                return context.getResources().getString(R.string.viewEntryPageTabJisho);
            default:
                throw new IllegalArgumentException("view entry fragment position is not 0 or 1");
        }
    }
}
