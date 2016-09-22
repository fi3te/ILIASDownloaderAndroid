package com.github.fi3te.iliasdownloader.model.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.view.fragment.viewpager.CurrentSyncFragment;
import com.github.fi3te.iliasdownloader.view.fragment.viewpager.LastDownloadsFragment;
import com.github.fi3te.iliasdownloader.view.fragment.viewpager.LocalFilesFragment;

/**
 * Created by wennier on 10.09.2015.
 */
public class IliasPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String[] pages;

    public IliasPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        pages = new String[]{context.getResources().getString(R.string.local), context.getResources().getString(R.string.current), context.getResources().getString(R.string.last)};
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LocalFilesFragment();
            case 1:
                return new CurrentSyncFragment();
            default:
                return new LastDownloadsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages[position];
    }
}