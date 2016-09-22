package com.github.fi3te.iliasdownloader.view.util;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

import com.github.fi3te.iliasdownloader.R;

/**
 * Created by wennier on 04.06.2015.
 */
public class DialogUtil {

    public static void showImprint(Activity context) {
        new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.imprint))
                .customView(context.getLayoutInflater().inflate(R.layout.dialog_imprint, null), false)
                .show();
    }

}
