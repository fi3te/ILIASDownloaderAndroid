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
import com.github.fi3te.iliasdownloader.view.LicencesActivity;
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
                activity.startActivity(new Intent(activity, LicencesActivity.class));
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
                                + activity.getResources().getString(R.string.created_in_cooperation_with_xxx).replaceAll("xxx", "Kevin Krummenauer"))
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
