package com.github.fi3te.iliasdownloader.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.whiledo.iliasdownloader2.syncrunner.service.IliasProperties;
import de.whiledo.iliasdownloader2.util.FileObject;
import de.whiledo.iliasdownloader2.util.TwoObjectsX;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.FragmentToActivityConnector;
import com.github.fi3te.iliasdownloader.ilias.IliasController;
import com.github.fi3te.iliasdownloader.ilias.IliasPropertiesUtil;
import com.github.fi3te.iliasdownloader.ilias.Session;
import com.github.fi3te.iliasdownloader.ilias.interfaces.IliasUI;
import com.github.fi3te.iliasdownloader.model.pageradapter.IliasPagerAdapter;
import com.github.fi3te.iliasdownloader.view.fragment.task.LoadCoursesDialogFragment;
import com.github.fi3te.iliasdownloader.view.preferences.SettingsActivity;
import com.github.fi3te.iliasdownloader.view.util.MenuItemSelectedUtil;

/**
 * Created by wennier on 04.09.2015.
 */
public class IliasActivity extends AppCompatActivity implements IliasUI, LoadCoursesDialogFragment.LoadCourses, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static String TASK_FRAGMENT_TAG = "task";

    private IliasController ilias;

    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private Snackbar snackbar;

    private IliasPagerAdapter adapter;

    private MaterialDialog chooseCoursesDialog;
    private Menu menu;

    private Runnable pendingRunnable;

    private FragmentToActivityConnector fragmentToActivityConnector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilias);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (pendingRunnable != null) {
                    new Handler().post(pendingRunnable);
                    pendingRunnable = null;
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
//        adapter = new OverviewPagerAdapter(getSupportFragmentManager(), this);
        adapter = new IliasPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE).setAction(R.string.close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        ilias = IliasController.getInstance(this);

        if (!ilias.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        fragmentToActivityConnector = FragmentToActivityConnector.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        ilias.registerUI(this);
        Session.loadLastDownloads(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationView();
    }

    public void updateNavigationView() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.login_item).setVisible(!ilias.isLoggedIn());
        menu.findItem(R.id.logout_item).setVisible(ilias.isLoggedIn());

        TextView usernameTextView = (TextView) navigationView.findViewById(R.id.usernameTextView);
        String username = ilias.isLoggedIn() ? IliasPropertiesUtil.readIliasProperties(this).getUserName() : "";
        if(usernameTextView != null) {
            usernameTextView.setText(username);
        }

        if (ilias.isLoggedIn()) {
            showCoursesItem();
        } else {
            hideCoursesItem();
        }
    }

    @Override
    public void onStop() {
        if (chooseCoursesDialog != null) {
            chooseCoursesDialog.dismiss();
            chooseCoursesDialog = null;
        }

        ilias.unregisterUI();
        Session.saveLastDownloads(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            boolean callSuper = fragmentToActivityConnector.onBackPressed(viewPager.getCurrentItem());
            if (callSuper) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (ilias.isLoggedIn()) {
            showCoursesItem();
        }
        return true;
    }

    public void showCoursesItem() {
        if (menu != null) {
            menu.findItem(R.id.coursesItem).setVisible(true);
        }
    }

    public void hideCoursesItem() {
        if (menu != null) {
            menu.findItem(R.id.coursesItem).setVisible(false);
        }
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        return MenuItemSelectedUtil.onOptionsItemSelected(this, item);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        pendingRunnable = new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()) {
                    case R.id.ignore_list_item:
                        if (!ilias.isRunning()) {
                            startActivity(new Intent(IliasActivity.this, BlockedFilesActivity.class));
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.synchronisation_is_running, Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.recommendation_item:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.recommendation_text));
                        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.recommend_by)));
                        break;
                    case R.id.rating_item:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.github.fi3te.iliasdownloader")));
                        break;
                    case R.id.bug_report_item:
                        String versionName = "X.X";
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            versionName = getResources().getString(R.string.version) + " " + packageInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "fiete.wennier@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " " + versionName);
                        startActivity(Intent.createChooser(emailIntent, "E-Mail"));
                        break;
                    case R.id.settings_item:
                        startActivity(new Intent(IliasActivity.this, SettingsActivity.class));
                        break;
                    case R.id.login_item:
                        startActivity(new Intent(IliasActivity.this, LoginActivity.class));
                        break;
                    case R.id.logout_item:
                        if (ilias.logout()) {
                            updateNavigationView();
                        } else {
                            Snackbar.make(coordinatorLayout, R.string.synchronisation_is_running, Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(fab)) {
            if (ilias.isLoggedIn()) {
                if (ilias.isRunning()) {
                    /*
                     * stopSync wird, wenn es vor der ersten Datei aufgerufen wird, ignoriert! // TODO an Kevin melden
                     */
                    if (Session.getSynchronisationFiles().size() > 0) {
                        ilias.stopSync();
                        updateFabSyncFinished();
                    }
                } else {
                    updateFabSyncRunning();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        public Void doInBackground(Void... params) {
                            ilias.startSync();
                            return null;
                        }
                    }.execute();
                }
            } else {
                Snackbar.make(coordinatorLayout, R.string.please_login, Snackbar.LENGTH_SHORT).setAction(R.string.open, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(IliasActivity.this, LoginActivity.class));
                    }
                }).show();
            }
        }
    }

    @Override
    public void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snackbar.setText(message);
                if (message != null && !message.isEmpty()) {
                    snackbar.show();
                } else {
                    snackbar.dismiss();
                }
            }
        });
    }

    @Override
    public void publishProgress(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(percent);
            }
        });
    }

    @Override
    public void newDownload() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentToActivityConnector.newDownload();
            }
        });
    }

    @Override
    public void updateCurrentSynchronisation(final FileObject fileObject) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentToActivityConnector.updateCurrentSynchronisation(fileObject);
            }
        });
    }

    @Override
    public void syncFinishedOrStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentToActivityConnector.syncFinishedOrStopped();
                updateFabSyncFinished();
                publishProgress(100); // fuer den Fall des Stops
            }
        });
    }


    public void updateFabSyncRunning() {
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_24dp));

        // TODO
    }

    private void updateFabSyncFinished() {
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync_white_24dp));

        // TODO
    }


    @Override
    public void coursesLoaded(TwoObjectsX<List<Long>, List<String>> coursesWithNames) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment task = fm.findFragmentByTag(TASK_FRAGMENT_TAG);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(task);
        ft.commitAllowingStateLoss();
        fm.executePendingTransactions();


        final List<Long> courses = coursesWithNames.getObjectA();
        List<String> courseNames = coursesWithNames.getObjectB();


        IliasProperties properties = IliasPropertiesUtil.readIliasProperties(this);
        Set<Long> activeCourses = properties.getActiveCourses();

        final CharSequence[] items = new CharSequence[courses.size()];
        for (int i = 0; i < courses.size(); i++) {
            items[i] = courseNames.get(i);
        }


        /*
         * Selected indices
         *    |
         *    V
         */
        List<Integer> selectedIndicesList = new LinkedList<Integer>();
        if (properties.isSyncAll()) {
            for (int i = 0; i < courses.size(); i++) {
                selectedIndicesList.add(i);
            }
        } else {
            for (int i = 0; i < courses.size(); i++) {
                if (activeCourses.contains(courses.get(i))) {
                    selectedIndicesList.add(i);
                }
            }
        }
        Integer[] selectedIndicesArray = new Integer[selectedIndicesList.size()];
        selectedIndicesList.toArray(selectedIndicesArray);


        chooseCoursesDialog = new MaterialDialog.Builder(this)
                .title(R.string.choose_courses)
                .items(items)
                .itemsCallbackMultiChoice(selectedIndicesArray, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        return true;
                    }
                })
                .positiveText(android.R.string.ok)
                .neutralText(R.string.select_all)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Integer[] selectedIndices = dialog.getSelectedIndices();
                        Set<String> newActiveCourses = new HashSet<String>();
                        for (Integer index : selectedIndices) {
                            newActiveCourses.add(Long.toString(courses.get(index)));
                        }
                        IliasPropertiesUtil.setActiveCourses(IliasActivity.this, newActiveCourses);
                        boolean syncAll = (selectedIndices.length == items.length);
                        IliasPropertiesUtil.setSyncAll(IliasActivity.this, syncAll);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        Set<String> newActiveCourses = new HashSet<String>();
                        for (int i = 0; i < courses.size(); i++) {
                            newActiveCourses.add(Long.toString(courses.get(i)));
                        }
                        IliasPropertiesUtil.setActiveCourses(IliasActivity.this, newActiveCourses);
                        IliasPropertiesUtil.setSyncAll(IliasActivity.this, true);
                    }
                })
                .build();
        chooseCoursesDialog.show();
    }
}