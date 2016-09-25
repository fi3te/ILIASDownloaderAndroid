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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.List;

import com.github.fi3te.iliasdownloader.R;

/**
 * Created by wennier on 04.09.2015.
 */
public class SimpleFileRecyclerAdapter extends RecyclerView.Adapter<SimpleFileRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<File> allFiles;
    private View.OnClickListener onClickListener;

    private LayoutInflater inflater;

    public SimpleFileRecyclerAdapter(Context context, List<File> allFiles, View.OnClickListener onClickListener) {
        this.context = context;
        this.allFiles = allFiles;
        this.onClickListener = onClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View root = inflater.inflate(R.layout.simple_file_list_entry, viewGroup, false);
        ViewHolder holder = new ViewHolder(root);

        root.setOnClickListener(onClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        File file = allFiles.get(i);

        String type = file.isDirectory() ? "" : FilenameUtils.getExtension(file.getPath());
        int typeLength = type.length();
        while (type.length() < 4)
            type = type + " ";
        holder.typeText.setText(type);

        if (file.isDirectory()) {
            holder.typeText.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_folder2));
        } else {
            holder.typeText.setBackgroundDrawable(null);
        }

        String name = file.getName();
        name = name.substring(0, name.length() - ((typeLength == 0) ? 0 : (typeLength + 1)));
        holder.nameText.setText(name);

        if (!file.getName().equals(".."))
            holder.dateText.setText(new DateTime(file.lastModified()).toString("dd.MM.yyyy"));
        else
            holder.dateText.setText("");
    }

    public File getItem(int position) {
        return allFiles.get(position);
    }

    @Override
    public int getItemCount() {
        return allFiles.size();
    }

    public void updateData(List<File> files) {
        this.allFiles = files;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeText;
        TextView nameText;
        TextView dateText;

        public ViewHolder(View itemView) {
            super(itemView);
            typeText = (TextView) itemView.findViewById(R.id.typeText);
            nameText = (TextView) itemView.findViewById(R.id.nameText);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
        }
    }
}
