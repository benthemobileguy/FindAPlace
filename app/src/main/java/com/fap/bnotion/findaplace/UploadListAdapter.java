package com.fap.bnotion.findaplace;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{
    public List<String> fileNameList;
    public List<String> fileDoneList;
    public ProgressBar mProgress;
    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList, ProgressBar progressBar){
        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
        this.mProgress = progressBar;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_single_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    String fileName = fileNameList.get(position);
    holder.fileNameView.setText(fileName);
    String fileDone = fileDoneList.get(position);
    if(fileDone.equals("uploading")){
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.fileDoneView.setVisibility(View.GONE);

    } else {
        holder.fileDoneView.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
    }
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
     View mView;
     public TextView fileNameView;
     public ProgressBar progressBar;
     public ImageView fileDoneView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            fileNameView = mView.findViewById(R.id.name_img);
            fileDoneView = mView.findViewById(R.id.done_img);
            progressBar = mView.findViewById(R.id.progress);
        }
    }
}
