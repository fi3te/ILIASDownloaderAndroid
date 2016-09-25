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
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.FragmentToActivityConnector;
import com.github.fi3te.iliasdownloader.controller.Util;
import com.github.fi3te.iliasdownloader.ilias.IliasController;
import com.github.fi3te.iliasdownloader.ilias.Session;
import com.github.fi3te.iliasdownloader.model.adapter.SimpleFileRecyclerAdapter;

/**
 * Created by wennier on 31.05.2015.
 */
public class LastDownloadsFragment extends Fragment implements View.OnClickListener, ViewPagerFragment {

    private IliasController ilias;

    private LinearLayout lastDownloadsView;
    private Button deleteHistoryButton;
    private RecyclerView recyclerView;
    private SimpleFileRecyclerAdapter adapter;

    private FragmentToActivityConnector fragmentToActivityConnector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentToActivityConnector = FragmentToActivityConnector.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lastDownloadsView = (LinearLayout) inflater.inflate(R.layout.fragment_last_downloads, container, false);

        ilias = IliasController.getInstance(getActivity());

        deleteHistoryButton = (Button) lastDownloadsView.findViewById(R.id.deleteHistoryButton);
        deleteHistoryButton.setOnClickListener(this);

        recyclerView = (RecyclerView) lastDownloadsView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SimpleFileRecyclerAdapter(getActivity(), Session.getLastDownloads(), this);
        recyclerView.setAdapter(adapter);

        return lastDownloadsView;
    }


    @Override
    public void onStart() {
        super.onStart();
        fragmentToActivityConnector.attach(this);
    }

    @Override
    public void onStop() {
        fragmentToActivityConnector.detach(this);
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        if (v.equals(deleteHistoryButton)) {
            Session.deleteDownloadHistory();
            adapter.updateData(Session.getLastDownloads());
        } else { // item of recyclerView
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            if(itemPosition < adapter.getItemCount()) {
                File file = adapter.getItem(itemPosition);
                Util.openFile(file, getActivity());
            }
        }
    }




    @Override
    public void newDownload() {
        adapter.updateData(Session.getLastDownloads());
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
        if(page == 2) {
//            callSuper = false;
        }
        return callSuper;
    }
}
