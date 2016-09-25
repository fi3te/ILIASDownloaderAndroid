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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.PreferencesUtil;
import com.github.fi3te.iliasdownloader.ilias.IliasController;
import com.github.fi3te.iliasdownloader.ilias.IliasPropertiesUtil;
import com.github.fi3te.iliasdownloader.view.fragment.task.LoginDialogFragment;
import com.github.fi3te.iliasdownloader.view.preferences.SettingsActivity;
import com.github.fi3te.iliasdownloader.view.preferences.SettingsFragment;
import com.github.fi3te.iliasdownloader.view.util.KeyboardUtil;
import com.github.fi3te.iliasdownloader.view.util.MenuItemSelectedUtil;

/**
 * Created by wennier on 08.09.2015.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginDialogFragment.LoginListener {

    public static String TASK_FRAGMENT_TAG = "task";

    private Toolbar toolbar;

    private CoordinatorLayout coordinatorLayout;
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button chooseIliasButton;
    private Button loginButton;
    private Button manualConfigurationButton;
    private Button continueWithoutLoginButton;

    private IliasController ilias;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        usernameEdit.setText(IliasPropertiesUtil.readIliasProperties(this).getUserName());
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        chooseIliasButton = (Button) findViewById(R.id.chooseIliasButton);
        chooseIliasButton.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        manualConfigurationButton = (Button) findViewById(R.id.manualConfigurationButton);
        manualConfigurationButton.setOnClickListener(this);
        continueWithoutLoginButton = (Button) findViewById(R.id.continueWithoutLoginButton);
        continueWithoutLoginButton.setOnClickListener(this);

        ilias = IliasController.getInstance(this);

        Eula.show(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return MenuItemSelectedUtil.onOptionsItemSelected(this, item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int delay = 200;

        if (usernameEdit.getText().toString().isEmpty()) {
            usernameEdit.requestFocus();
            usernameEdit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyboardUtil.showKeyboard(LoginActivity.this, usernameEdit);
                }
            }, delay);
        } else {
            passwordEdit.requestFocus();
            passwordEdit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyboardUtil.showKeyboard(LoginActivity.this, passwordEdit);
                }
            }, delay);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(chooseIliasButton)) {
            SharedPreferences preferences = PreferencesUtil.getPreferences(this);
            SettingsFragment.chooseIlias(preferences, this);
        } else if (v.equals(loginButton)) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment loginTask = new LoginDialogFragment();
            Bundle args = new Bundle();
            args.putString(LoginDialogFragment.USERNAME, usernameEdit.getText().toString());
            args.putString(LoginDialogFragment.PASSWORD, passwordEdit.getText().toString());
            loginTask.setArguments(args);
            fm.beginTransaction().add(loginTask, TASK_FRAGMENT_TAG).commit();
            fm.executePendingTransactions();
        } else if (v.equals(manualConfigurationButton)) {
            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
        } else if (v.equals(continueWithoutLoginButton)) {
            finish();
        }
    }

    @Override
    public void loggedIn(boolean successful) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment task = fm.findFragmentByTag(TASK_FRAGMENT_TAG);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(task);
        ft.commit();

        if (successful) {
            finish();
        } else {
            Snackbar.make(coordinatorLayout, R.string.login_failed, Snackbar.LENGTH_SHORT).show();
        }
    }
}
