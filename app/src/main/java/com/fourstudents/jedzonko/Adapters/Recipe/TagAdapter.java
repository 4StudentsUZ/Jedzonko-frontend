package com.fourstudents.jedzonko.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolderClass> {
    Context context;
    List<Tag> tagList;

    private final OnTagListener onTagListener;

    public TagAdapter(Context context, List<Tag> tagList, OnTagListener onTagListener) {
        this.context = context;
        this.tagList =tagList;
        this.onTagListener = onTagListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view, onTagListener);
            return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        Tag tag = tagList.get(position);
        holder.textView.setText(tag.getName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        OnTagListener onTagListener;

        public ViewHolderClass(@NonNull View itemView, OnTagListener onTagListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemListTextView);
            this.onTagListener = onTagListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTagListener.onTagClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnTagListener {
        void onTagClick(int position);
    }

}
