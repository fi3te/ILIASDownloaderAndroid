package com.github.fi3te.iliasdownloader.controller;

import java.util.ArrayList;
import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.view.fragment.viewpager.ViewPagerFragment;

/**
 * Created by wennier on 13.08.2015.
 */
public class FragmentToActivityConnector implements ViewPagerFragment {

    private static FragmentToActivityConnector instance = new FragmentToActivityConnector();

    private List<ViewPagerFragment> list;

    private FragmentToActivityConnector() {
        list = new ArrayList<>();
    }

    public static FragmentToActivityConnector getInstance() {
        return instance;
    }

    public void attach(ViewPagerFragment viewPagerFragment) {
        list.add(viewPagerFragment);
    }

    public void detach(ViewPagerFragment viewPagerFragment) {
        list.remove(viewPagerFragment);
    }

    @Override
    public void showMessage(String message) {
        for (ViewPagerFragment viewPagerFragment : list) {
            viewPagerFragment.showMessage(message);
        }
    }

    @Override
    public void publishProgress(int percent) {
        for (ViewPagerFragment viewPagerFragment : list) {
            viewPagerFragment.publishProgress(percent);
        }
    }

    @Override
    public void newDownload() {
        for (ViewPagerFragment viewPagerFragment : list) {
            viewPagerFragment.newDownload();
        }
    }

    @Override
    public void updateCurrentSynchronisation(FileObject fileObject) {
        for (ViewPagerFragment viewPagerFragment : list) {
            viewPagerFragment.updateCurrentSynchronisation(fileObject);
        }
    }

    @Override
    public void syncFinishedOrStopped() {
        for (ViewPagerFragment viewPagerFragment : list) {
            viewPagerFragment.syncFinishedOrStopped();
        }
    }

    @Override
    public boolean onBackPressed(int page) {
        for (ViewPagerFragment viewPagerFragment : list) {
            boolean returnIfFalse = viewPagerFragment.onBackPressed(page);
            if (!returnIfFalse)
                return false;
        }
        return true;
    }
}
