/*
 * Copyright (C) 2015-2016 Fiete Wennier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 *
 */

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