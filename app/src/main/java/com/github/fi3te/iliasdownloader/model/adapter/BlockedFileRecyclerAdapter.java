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

import java.util.List;

import com.github.fi3te.iliasdownloader.R;
import com.github.fi3te.iliasdownloader.model.database.BlockedFile;

/**
 * Created by wennier on 12.09.2015.
 */
public class BlockedFileRecyclerAdapter extends RecyclerView.Adapter<BlockedFileRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<BlockedFile> allFiles;
    private View.OnClickListener onClickListener;

    private LayoutInflater inflater;

    public BlockedFileRecyclerAdapter(Context context, List<BlockedFile> allFiles, View.OnClickListener onClickListener) {
        this.context = context;
        this.allFiles = allFiles;
        this.onClickListener = onClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View root = inflater.inflate(R.layout.blocked_file_list_entry, viewGroup, false);
        ViewHolder holder = new ViewHolder(root);

        root.setOnClickListener(onClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        BlockedFile file = allFiles.get(i);
        holder.filenameText.setText(file.getFilename());
        holder.fileSizeText.setText("" + Math.round(file.getFileSize() / 1024.0f * 100.0f) / 100.0f + " KiB");
    }

    public BlockedFile getItem(int position) {
        return allFiles.get(position);
    }

    @Override
    public int getItemCount() {
        return allFiles.size();
    }

    public void removeItem(int position) {
        allFiles.remove(position);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView filenameText;
        TextView fileSizeText;

        public ViewHolder(View itemView) {
            super(itemView);
            filenameText = (TextView) itemView.findViewById(R.id.filenameText);
            fileSizeText = (TextView) itemView.findViewById(R.id.fileSizeText);
        }
    }
}
