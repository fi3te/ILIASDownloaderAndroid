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

package com.github.fi3te.iliasdownloader.view.util;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.view.IliasActivity;
import com.github.fi3te.iliasdownloader.view.LicensesActivity;
import com.github.fi3te.iliasdownloader.view.fragment.task.LoadCoursesDialogFragment;

/**
 * Created by wennier on 10.09.2015.
 */
public class MenuItemSelectedUtil {

    public static boolean onOptionsItemSelected(final FragmentActivity activity, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.coursesItem:
                FragmentManager fm = activity.getSupportFragmentManager();
                Fragment loadCoursesTask = new LoadCoursesDialogFragment();
                fm.beginTransaction().add(loadCoursesTask, IliasActivity.TASK_FRAGMENT_TAG).commit();
                fm.executePendingTransactions();
                return true;
            case R.id.licencesItem:
                activity.startActivity(new Intent(activity, LicensesActivity.class));
                return true;
            case R.id.imprintItem:
                DialogUtil.showImprint(activity);
                return true;
            case R.id.aboutItem:
                String version = null;
                try {
                    PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                    version = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                final String v = (version == null) ? "" : (" " + version);
                new MaterialDialog.Builder(activity)
                        .icon(activity.getResources().getDrawable(R.mipmap.ic_launcher))
                        .title(activity.getResources().getString(R.string.about))
                        .content(activity.getResources().getString(R.string.app_name) + ((version != null) ? (" " + version) : "") + "\n"
                                + "Fiete Wennier" + "\n"
                                + "https://github.com/fi3te/ILIASDownloaderAndroid")
                        .positiveText(activity.getResources().getString(R.string.support))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "fiete.wennier@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.app_name) + v);
                                activity.startActivity(Intent.createChooser(emailIntent, "E-Mail"));

                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
        }
        return false;
    }

}
