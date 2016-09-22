package com.github.fi3te.iliasdownloader.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.DatabaseController;
import com.github.fi3te.iliasdownloader.model.adapter.BlockedFileRecyclerAdapter;
import com.github.fi3te.iliasdownloader.model.database.BlockedFile;
import com.github.fi3te.iliasdownloader.view.util.MenuItemSelectedUtil;

/**
 * Created by wennier on 12.09.2015.
 */
public class BlockedFilesActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseController dController;

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private BlockedFileRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_files);

        dController = DatabaseController.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BlockedFileRecyclerAdapter(this, dController.getAllBlockedFiles(), this);
        recyclerView.setAdapter(adapter);

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
    public void onClick(View v) {
        final int itemPosition = recyclerView.getChildLayoutPosition(v);
        final BlockedFile file = adapter.getItem(itemPosition);
        new MaterialDialog.Builder(this)
                .title(R.string.remove)
                .content(file.getFilename())
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dController.deleteBlockedFileByRefId(file.getRefId());
                        adapter.removeItem(itemPosition);
                    }
                })
                .show();
    }
}
