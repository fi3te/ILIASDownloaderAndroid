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

package com.github.fi3te.iliasdownloader.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.PreferencesUtil;

/**
 * Created by wennier on 25.09.2016.
 */

public class Eula extends DialogFragment {

    public static final String EULA_FFRAGMENT_TAG = "eula";

    private static final String EULA_PREFIX = "eula_";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        Activity activity = getActivity();

        PackageInfo versionInfo = getPackageInfo(activity);
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences preferences = PreferencesUtil.getPreferences(activity);

        String title = activity.getString(R.string.app_name) + " v"
                + versionInfo.versionName;

        String message = activity.getString(R.string.eula);

        return new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(eulaKey, true);
                        editor.commit();

                        FragmentManager fm = getFragmentManager();
                        Fragment eula = fm.findFragmentByTag(EULA_FFRAGMENT_TAG);
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(eula);
                        ft.commitAllowingStateLoss();
                        fm.executePendingTransactions();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().finishAffinity();
                    }
                })
                .cancelable(false)
                .build();
    }

    private static PackageInfo getPackageInfo(Activity activity) {
        PackageInfo pi = null;
        try {
            pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public static void show(AppCompatActivity activity) {
        PackageInfo versionInfo = getPackageInfo(activity);
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences preferences = PreferencesUtil.getPreferences(activity);

        boolean hasBeenShown = preferences.getBoolean(eulaKey, false);
        if (hasBeenShown == false) {
            FragmentManager fm = activity.getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag(EULA_FFRAGMENT_TAG);
            if (f == null) {
                Fragment eulaFragment = new Eula();
                fm.beginTransaction().add(eulaFragment, EULA_FFRAGMENT_TAG).commit();
                fm.executePendingTransactions();
            }
        }
    }

}
