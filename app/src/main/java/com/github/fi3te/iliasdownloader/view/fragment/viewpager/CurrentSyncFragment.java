package com.github.fi3te.iliasdownloader.view.fragment.viewpager;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import de.whiledo.iliasdownloader2.util.DownloadMethod;
import de.whiledo.iliasdownloader2.util.FileObject;
import de.whiledo.iliasdownloader2.util.SyncState;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.DatabaseController;
import com.github.fi3te.iliasdownloader.controller.FragmentToActivityConnector;
import com.github.fi3te.iliasdownloader.controller.Util;
import com.github.fi3te.iliasdownloader.controller.courses.DataProviderAdapterConnector;
import com.github.fi3te.iliasdownloader.controller.courses.StaticSortedCourseDataProvider;
import com.github.fi3te.iliasdownloader.ilias.IliasController;
import com.github.fi3te.iliasdownloader.ilias.Session;
import com.github.fi3te.iliasdownloader.model.adapter.CourseRecyclerAdapter;
import com.github.fi3te.iliasdownloader.view.IliasActivity;

/**
 * Created by wennier on 05.09.2015.
 */
public class CurrentSyncFragment extends Fragment implements ExpandableListView.OnChildClickListener, ViewPagerFragment {

    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";

    private DatabaseController dController;
    private IliasController ilias;

    private RecyclerView recyclerView;
    private CourseRecyclerAdapter adapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;

    private FragmentToActivityConnector fragmentToActivityConnector;

    private DataProviderAdapterConnector connector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentToActivityConnector = FragmentToActivityConnector.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.vertical_recycler_view, null);

        dController = DatabaseController.getInstance(getActivity());
        ilias = IliasController.getInstance(getActivity());

        List<FileObject> list = new ArrayList<>();
        list = Session.getSynchronisationFiles();

        final Parcelable expandableItemManagerSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(expandableItemManagerSavedState);

        adapter = new CourseRecyclerAdapter(getActivity()/*, ExpandableCourse.createCourses(list)*/, this, mRecyclerViewExpandableItemManager);

        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mWrappedAdapter);
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        recyclerView.setHasFixedSize(false);

        mRecyclerViewExpandableItemManager.attachRecyclerView(recyclerView);

        connector = new DataProviderAdapterConnector(StaticSortedCourseDataProvider.getInstance(), mRecyclerViewExpandableItemManager, adapter);

        return recyclerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER, mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
            mRecyclerViewExpandableItemManager = null;
        }
        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentToActivityConnector.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        connector.synchronizeWithSession();
    }

    @Override
    public void onStop() {
        fragmentToActivityConnector.detach(this);
        super.onStop();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
        if (!ilias.isRunning()) {
            final FileObject fileObject = (FileObject) adapter.getChild(groupPosition, childPosition);

            final String ITEM_OPEN = getResources().getString(R.string.open);
            final String ITEM_DOWNLOAD = getResources().getString(R.string.download);
            final String ITEM_DELETE = getResources().getString(R.string.delete);
            final String ITEM_DELETE_AND_IGNORE = getResources().getString(R.string.delete_and_ignore);

            List<CharSequence> list = new ArrayList<>();

            SyncState s = fileObject.getSyncState();

            if (s.equals(SyncState.ALREADY_UP_TO_DATE) || s.equals(SyncState.UPDATED)) {
                list.add(ITEM_OPEN);
                list.add(ITEM_DELETE);
                list.add(ITEM_DELETE_AND_IGNORE);
            } else {
                list.add(ITEM_DOWNLOAD);
            }

            CharSequence[] items = new CharSequence[0];
            items = list.toArray(items);

            new MaterialDialog.Builder(getActivity())
                    .title(fileObject.getTargetFile().getName())
                    .content(s.getReadableName()
                            + "\n" + (fileObject.getFileSize() / 1024) + " KiB"
                            + "\n" + new DateTime(fileObject.getLastUpdated()).toString("dd.MM.yyyy HH:mm"))
                    .items(items)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            final String item = charSequence.toString();

                            if (item.equals(ITEM_OPEN)) {
                                if (fileObject.getTargetFile() != null) {
                                    Util.openFile(fileObject.getTargetFile(), getActivity());
                                }
                            } else if (item.equals(ITEM_DOWNLOAD)) {
                                if (fileObject.getSyncState().equals(SyncState.IGNORED_IGNORE_LIST)) {
                                    dController.deleteBlockedFileByRefId(fileObject.getRefId());
                                }
                                ilias.downloadFile(fileObject, DownloadMethod.WEBSERVICE);
                                adapter.removeChild(groupPosition, childPosition);
                            } else if (item.equals(ITEM_DELETE)) {
                                if (fileObject.getTargetFile() != null) {
                                    if (fileObject.getTargetFile().delete()) {
                                        adapter.removeChild(groupPosition, childPosition);
                                        // update localfilesfragment and lastdownloadsfragment
                                        Session.removeFromHistory(fileObject.getTargetFile());
                                        ((IliasActivity) getActivity()).newDownload();
                                    }
                                }
                            } else if (item.equals(ITEM_DELETE_AND_IGNORE)) {
                                if (fileObject.getTargetFile() != null) {
                                    if (fileObject.getTargetFile().delete()) {
                                        dController.insertBlockedFile(fileObject.getRefId(), fileObject.getTargetFile().getName(), fileObject.getFileSize());
                                        adapter.removeChild(groupPosition, childPosition);
                                        // update localfilesfragment and lastdownloadsfragment
                                        Session.removeFromHistory(fileObject.getTargetFile());
                                        ((IliasActivity) getActivity()).newDownload();
                                    }
                                }
                            }
                        }
                    })
                    .show();
        }
        return true;
    }


    @Override
    public void newDownload() {
        // do nothing
    }

    @Override
    public void updateCurrentSynchronisation(FileObject fileObject) {
        if (fileObject == null) { // register UI or start of the sync
            connector.synchronizeWithSession();
        } else {
            connector.insertOrUpdate(fileObject);
        }
    }

    @Override
    public void syncFinishedOrStopped() {
        // do nothing
    }

    @Override
    public void publishProgress(int percent) {
        // do nothing
    }

    @Override
    public void showMessage(String message) {
        // do nothing
    }

    @Override
    public boolean onBackPressed(int page) {
        boolean callSuper = true;
        if (page == 1) {
//            callSuper = false;
        }
        return callSuper;
    }
}

