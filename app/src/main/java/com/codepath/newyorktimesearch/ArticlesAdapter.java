package com.codepath.newyorktimesearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by klimjinx on 6/20/16.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the view within the item layout for the fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Holder contains member variable for any view that will be set as you render a row
        public ImageView ivImage;
        public TextView tvTitle;
        private Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            this.context = context; // Store the context for itemClick
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Article article = articles.get(position);
            // We can access the data within the views
            Toast.makeText(context, tvTitle.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    // Store a member variable for the articles
    private List<Article> mArticles;

    // Pass in the contact array into the constructor
    public ArticlesAdapter(List<Article> articles) {
        mArticles = articles;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on the data model
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());

        ImageView imageView = viewHolder.ivImage;
        // clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        // populate the thumbnail image & remote download image in the background
        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(viewHolder.ivImage.getContext()).load(thumbnail).into(imageView);
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}