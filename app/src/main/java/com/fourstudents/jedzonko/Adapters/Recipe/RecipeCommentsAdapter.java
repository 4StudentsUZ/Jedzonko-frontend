package com.fourstudents.jedzonko.Adapters.Recipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Other.CommentItem;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

public class RecipeCommentsAdapter extends ListAdapter<CommentItem, RecipeCommentsAdapter.ViewHolderClass> {
    Context context;
    public RecipeCommentsAdapter(Context context) {
        super(new DiffUtil.ItemCallback<CommentItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull CommentItem oldItem, @NonNull CommentItem newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull CommentItem oldItem, @NonNull CommentItem newItem) {
                return oldItem == newItem;
            }


        });

        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_list, parent, false);
        RecipeCommentsAdapter.ViewHolderClass viewHolderClass = new RecipeCommentsAdapter.ViewHolderClass(view);
        return viewHolderClass;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
       CommentItem commentItem = getItem(position);
        holder.authorTextView.setText(commentItem.getAuthor()+", "+ commentItem.getDate());
        holder.commentTextView.setText(commentItem.getComment().trim()+"\n\n");
    }


    public static class ViewHolderClass extends RecyclerView.ViewHolder{
        TextView authorTextView;
        TextView commentTextView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
           authorTextView= itemView.findViewById(R.id.authorTextView);
           commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
