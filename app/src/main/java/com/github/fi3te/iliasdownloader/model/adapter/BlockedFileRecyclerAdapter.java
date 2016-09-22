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
