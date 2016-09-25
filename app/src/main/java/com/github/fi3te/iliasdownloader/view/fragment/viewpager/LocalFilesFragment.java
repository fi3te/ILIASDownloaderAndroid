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
