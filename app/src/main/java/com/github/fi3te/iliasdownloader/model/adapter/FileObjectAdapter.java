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

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import de.whiledo.iliasdownloader2.util.FileObject;
import com.github.fi3te.iliasdownloader.R;

/**
 * Created by wennier on 03.06.2015.
 */
public class FileObjectAdapter extends ArrayAdapter<FileObject> {

    public FileObjectAdapter(Context context, List<FileObject> allFiles) {
        super(context, R.layout.file_object_list_entry, allFiles);
    }

    static class ViewHolder {
        View syncStateView;
        TextView fileNameText;
        TextView lastUpdatedText;
        TextView fileSizeText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        ViewHolder holder;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.file_object_list_entry
                    , parent, false);

            holder = new ViewHolder();
            holder.syncStateView = rowView.findViewById(R.id.syncStateView);
            holder.fileNameText = (TextView) rowView.findViewById(R.id.fileNameText);
            holder.lastUpdatedText = (TextView) rowView.findViewById(R.id.lastUpdatedText);
            holder.fileSizeText = (TextView) rowView.findViewById(R.id.fileSizeText);
            rowView.setTag(holder);
        }

        holder = (ViewHolder) rowView.getTag();


        FileObject file = getItem(position);

        Resources r = getContext().getResources();
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

        return rowView;
    }

    public void updateData(List<FileObject> allFiles) {
        clear();
        addAll(allFiles);
        notifyDataSetChanged();
    }
}
