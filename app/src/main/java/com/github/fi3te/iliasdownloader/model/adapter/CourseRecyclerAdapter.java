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

package com.github.fi3te.iliasdownloader.model.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import org.joda.time.DateTime;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.controller.courses.AbstractDataProvider;
import com.github.fi3te.iliasdownloader.controller.courses.StaticSortedCourseDataProvider;
import com.github.fi3te.iliasdownloader.model.ExpandableCourse;

/**
 * Created by wennier on 05.09.2015.
 */
public class CourseRecyclerAdapter extends AbstractExpandableItemAdapter<CourseRecyclerAdapter.GroupViewHolder, CourseRecyclerAdapter.ChildViewHolder> {

    private Activity activity;
    private ExpandableListView.OnChildClickListener listener;
    private AbstractDataProvider<ExpandableCourse, FileObject> dataProvider;
    private RecyclerViewExpandableItemManager expandableItemManager;

    public static class GroupViewHolder extends AbstractExpandableItemViewHolder {
        RelativeLayout container;
        View indicatorView;
        TextView courseName;
        TextView alreadyUpToDateCount;
        TextView updatedCount;
        TextView ignoredCount;
        TextView loadingCount;
        TextView corruptCount;
        TextView errorCount;

        public GroupViewHolder(View v) {
            super(v);
            container = (RelativeLayout) v.findViewById(R.id.container);
            indicatorView = v.findViewById(R.id.indicatorView);
            courseName = (TextView) v.findViewById(R.id.courseName);
            alreadyUpToDateCount = (TextView) v.findViewById(R.id.alreadyUpToDateCount);
            updatedCount = (TextView) v.findViewById(R.id.updatedCount);
            ignoredCount = (TextView) v.findViewById(R.id.ignoredCount);
            loadingCount = (TextView) v.findViewById(R.id.loadingCount);
            corruptCount = (TextView) v.findViewById(R.id.corruptCount);
            errorCount = (TextView) v.findViewById(R.id.errorCount);
        }
    }

    public static class ChildViewHolder extends AbstractExpandableItemViewHolder {
        LinearLayout container;
        View syncStateView;
        TextView fileNameText;
        TextView lastUpdatedText;
        TextView fileSizeText;

        public ChildViewHolder(View v) {
            super(v);
            container = (LinearLayout) v.findViewById(R.id.container);
            syncStateView = v.findViewById(R.id.syncStateView);
            fileNameText = (TextView) v.findViewById(R.id.fileNameText);
            lastUpdatedText = (TextView) v.findViewById(R.id.lastUpdatedText);
            fileSizeText = (TextView) v.findViewById(R.id.fileSizeText);
        }
    }

    public CourseRecyclerAdapter(Activity activity, ExpandableListView.OnChildClickListener listener, RecyclerViewExpandableItemManager expandableItemManager) {
        this.activity = activity;
        dataProvider = StaticSortedCourseDataProvider.getInstance();
        this.listener = listener;
        this.expandableItemManager = expandableItemManager;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return dataProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return dataProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return dataProvider.getChildItem(groupPosition, childPosition);
    }

    public void removeChild(int groupPosition, int childPosition) {
        dataProvider.removeChildItem(groupPosition, childPosition);
        expandableItemManager.notifyChildItemRemoved(groupPosition, childPosition);
        expandableItemManager.notifyGroupItemChanged(groupPosition);
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public GroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.course_list_entry_with_indicator, parent, false);
        return new GroupViewHolder(v);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.file_object_list_entry, parent, false);
        return new ChildViewHolder(v);
    }

    private String threeSignsNumber(int number) {
        if (number < 10 && number > -1)
            return "00" + number;
        else if (number < 100 && number > 9)
            return "0" + number;
        else
            return "" + number;
    }

    @Override
    public void onBindGroupViewHolder(GroupViewHolder holder, int groupPosition, int viewType) {
        ExpandableCourse group = dataProvider.getGroupItem(groupPosition);

        holder.courseName.setText(group.getTitle());
        holder.alreadyUpToDateCount.setText(threeSignsNumber(group.getAlreadyUpToDate()));
        holder.updatedCount.setText(threeSignsNumber(group.getUpdated()));
        holder.ignoredCount.setText(threeSignsNumber(group.getIgnored()));
        holder.loadingCount.setText(threeSignsNumber(group.getLoading()));
        holder.corruptCount.setText(threeSignsNumber(group.getCorrupt()));
        holder.errorCount.setText(threeSignsNumber(group.getError()));

        final int expandState = holder.getExpandStateFlags();
        if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.ic_expand_less_black_18dp;
            } else {
                bgResId = R.drawable.ic_expand_more_black_18dp;
            }
            holder.indicatorView.setBackgroundResource(bgResId);
        }

        holder.container.setClickable(true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindChildViewHolder(ChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        FileObject file = dataProvider.getChildItem(groupPosition, childPosition);

        Resources r = activity.getResources();
        int color = r.getColor(R.color.green_500);
        switch (file.getSyncState()) {
            case ALREADY_UP_TO_DATE:
                // do nothing
                break;
            case LOADING:
                color = r.getColor(R.color.yellow_500);
                break;
            case UPDATED:
                color = r.getColor(R.color.teal_500);
                break;
            case CORRUPT:
                color = r.getColor(R.color.purple_500);
                break;
            case ERROR:
                color = r.getColor(R.color.red_500);
                break;
            default: // IGNORED_GREATER_MAX_SIZE || IGNORED_IGNORE_LIST || IGNORED_NO_DOWNLOAD_ALLOWED
                color = r.getColor(R.color.grey_500);
                break;
        }
        holder.syncStateView.setBackgroundColor(color);

        holder.fileNameText.setText(file.getTargetFile().getName());

        holder.lastUpdatedText.setText(new DateTime(file.getLastUpdated()).toString("dd.MM.yyyy HH:mm"));

        holder.fileSizeText.setText("" + Math.round(file.getFileSize() / 1024.0f * 100.0f) / 100.0f + " KiB");

        final int group = groupPosition;
        final int child = childPosition;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onChildClick(null, view, group, child, 0);
            }
        });
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(GroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        return holder.container.isClickable();
    }
}