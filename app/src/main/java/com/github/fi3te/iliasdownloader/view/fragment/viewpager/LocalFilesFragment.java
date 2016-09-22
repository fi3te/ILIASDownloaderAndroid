package com.github.fi3te.iliasdownloader.view.fragment.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.FileComparator;
import com.github.fi3te.iliasdownloader.controller.FragmentToActivityConnector;
import com.github.fi3te.iliasdownloader.controller.Util;
import com.github.fi3te.iliasdownloader.ilias.IliasController;
import com.github.fi3te.iliasdownloader.ilias.IliasPropertiesUtil;
import com.github.fi3te.iliasdownloader.model.IgnoreFilter;
import com.github.fi3te.iliasdownloader.model.adapter.SimpleFileRecyclerAdapter;

/**
 * Created by wennier on 21.07.2015.
 */
public class LocalFilesFragment extends Fragment implements ViewPagerFragment, View.OnClickListener {

    private IliasController ilias;

    private RecyclerView recyclerView;

    private SimpleFileRecyclerAdapter adapter;

    private static String currentDirectory = null; // wird beim ersten Start initialisiert

    private FragmentToActivityConnector fragmentToActivityConnector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentToActivityConnector = FragmentToActivityConnector.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(currentDirectory == null) {
            currentDirectory = IliasPropertiesUtil.readIliasProperties(getActivity()).getBaseDirectory();
        }

        ilias = IliasController.getInstance(getActivity());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.vertical_recycler_view, null);
        recyclerView.setAdapter(new SimpleFileRecyclerAdapter(getActivity(), new ArrayList<File>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return recyclerView;
    }

    @Override
    public void onClick(View view) {
        int itemPosition = recyclerView.getChildLayoutPosition(view);

        File file = adapter.getItem(itemPosition);

        if (file.isFile()) {
            Util.openFile(file, getActivity());
        } else if (file.isDirectory()) {
            if (file.getName().equals("..")) { // up
                File parentFile = file.getParentFile().getParentFile();
                currentDirectory = parentFile.getPath();
            } else { // down
                currentDirectory = file.getPath();
            }
            showLocalFiles();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentToActivityConnector.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLocalFiles();
    }

    @Override
    public void onStop() {
        fragmentToActivityConnector.detach(this);
        super.onStop();
    }



    @Override
    public void newDownload() {
        showLocalFiles();
    }

    @Override
    public void updateCurrentSynchronisation(FileObject fileObject) {
        // do nothing
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
        if(page == 0) {
            if(adapter.getItemCount() > 0) {
                File file = adapter.getItem(0);
                if (file.isDirectory() && file.getName().equals("..")) { // up
                    File parentFile = file.getParentFile().getParentFile();
                    currentDirectory = parentFile.getPath();
                    showLocalFiles();
                    callSuper = false;
                }
            }
        }
        return callSuper;
    }

    private void showLocalFiles() {
        List<File> files = new ArrayList<>();

        if(!currentDirectory.endsWith("/"))
            currentDirectory += "/";

        if (!currentDirectory.equals(IliasPropertiesUtil.readIliasProperties(getActivity()).getBaseDirectory())) {
            files.add(new File(currentDirectory + ".."));
        }

        List<File> list = Util.arrayToArrayList(new File(currentDirectory).listFiles(new IgnoreFilter()));
        Collections.sort(list, new FileComparator());
        files.addAll(list);

        if(adapter == null) {
            adapter = new SimpleFileRecyclerAdapter(getActivity(), files, this);
        } else {
            adapter.updateData(files);
        }
        recyclerView.setAdapter(adapter);
    }
}
