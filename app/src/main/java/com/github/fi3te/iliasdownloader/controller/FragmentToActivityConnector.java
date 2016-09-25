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
