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
            SharedPreferences preferences = getSharedPreferences("de.whiledo.iliasdownloaderandroid", Context.MODE_PRIVATE);
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
