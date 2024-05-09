package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<Article> articleList;
    private OnSaveButtonClickListener onSaveButtonClickListener;
    private Context context;

    public NewsAdapter(List<Article> articleList, OnSaveButtonClickListener onSaveButtonClickListener,Context context) {
        this.articleList = articleList;
        this.onSaveButtonClickListener = onSaveButtonClickListener;
        this.context=context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_recycler_row, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.titleTxt.setText(article.getTitle());
        if (article.getSource() != null) {
            holder.sourceTxt.setText(article.getSource().getName());
        } else {
            holder.sourceTxt.setText("Unknown Source");
        }
        Picasso.get().load(article.getUrlToImage())
                .error(R.drawable.hide_image)
                .placeholder(R.drawable.hide_image)
                .into(holder.articleImg);
        holder.saveBtn.setOnClickListener(view -> {
            if (onSaveButtonClickListener != null) {
                onSaveButtonClickListener.onSaveButtonClick(article);
            }
        });
        holder.itemView.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), DetailNewsActivity.class);
            intent.putExtra("url", article.getUrl());
            view.getContext().startActivity(intent);
        }));

        holder.shareBtn.setOnClickListener(view -> {

            // Create a share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, article.getUrl());

            // Start the share activity using the stored context
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    void updateData(List<Article> data) {
        articleList.clear();
        articleList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, sourceTxt;
        ImageView articleImg;
        ImageButton saveBtn,shareBtn;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.article_title);
            sourceTxt = itemView.findViewById(R.id.article_source);
            articleImg = itemView.findViewById(R.id.article_image);
            saveBtn = itemView.findViewById(R.id.saveBtn);
            shareBtn=itemView.findViewById(R.id.shareBtn);
        }
    }

    public interface OnSaveButtonClickListener {
        void onSaveButtonClick(Article article);
    }
}
