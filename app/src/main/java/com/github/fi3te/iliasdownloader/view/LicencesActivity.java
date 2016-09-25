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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import com.github.fi3te.iliasdownloader.R;

/**
 * Created by wennier on 04.06.2015.
 */
public class LicencesActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageButton iliasDownloaderButton;
    private ImageButton jodaTimeButton;
    private ImageButton simpleButton;
    private ImageButton materialDialogsButton;
    private ImageButton advancedRecyclerViewButton;
    private ImageButton ksoap2androidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licences);


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iliasDownloaderButton = (ImageButton) findViewById(R.id.iliasDownloaderButton);
        iliasDownloaderButton.setOnClickListener(this);

        jodaTimeButton = (ImageButton) findViewById(R.id.jodaTimeButton);
        jodaTimeButton.setOnClickListener(this);

        simpleButton = (ImageButton) findViewById(R.id.simpleButton);
        simpleButton.setOnClickListener(this);

        materialDialogsButton = (ImageButton) findViewById(R.id.materialDialogsButton);
        materialDialogsButton.setOnClickListener(this);

        advancedRecyclerViewButton = (ImageButton) findViewById(R.id.advancedRecyclerViewButton);
        advancedRecyclerViewButton.setOnClickListener(this);

        ksoap2androidButton = (ImageButton) findViewById(R.id.ksoap2androidButton);
        ksoap2androidButton.setOnClickListener(this);

        TextView materialDialogsTextView = (TextView) findViewById(R.id.materialDialogsTextView);
        TextView ksoap2androidTextView = (TextView) findViewById(R.id.ksoap2androidTextView);
        TextView apacheTextView = (TextView) findViewById(R.id.apacheTextView);

        try {
            InputStream is = getAssets().open("material_dialogs_license.txt");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String text = new String(buffer);

            materialDialogsTextView.setText(text);


            is = getAssets().open("ksoap2_android_license.txt");
            size = is.available();

            buffer = new byte[size];
            is.read(buffer);
            is.close();

            text = new String(buffer);

            ksoap2androidTextView.setText(text);


            is = getAssets().open("copy_of_apache_license.txt");
            size = is.available();

            buffer = new byte[size];
            is.read(buffer);
            is.close();

            text = new String(buffer);

            apacheTextView.setText(text);
        } catch (IOException e) {
            Log.e("LicencesActivity", e.toString());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.equals(iliasDownloaderButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.whiledo.de/")));
        } else if (v.equals(jodaTimeButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.joda.org/joda-time/")));
        } else if (v.equals(simpleButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://simple.sourceforge.net/")));
        } else if (v.equals(materialDialogsButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/afollestad/material-dialogs")));
        } else if (v.equals(advancedRecyclerViewButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/h6ah4i/android-advancedrecyclerview")));
        } else if (v.equals(ksoap2androidButton)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://code.google.com/p/ksoap2-android/")));
        }
    }
}