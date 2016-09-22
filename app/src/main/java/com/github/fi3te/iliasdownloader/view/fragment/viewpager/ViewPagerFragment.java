package com.github.fi3te.iliasdownloader.view.fragment.viewpager;

import com.github.fi3te.iliasdownloader.ilias.interfaces.IliasUI;

/**
 * Created by wennier on 13.08.2015.
 */
public interface ViewPagerFragment extends IliasUI {

    /**
     * @return call super
     */
    boolean onBackPressed(int page);

}
