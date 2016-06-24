package com.codepath.newyorktimesearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.newyorktimesearch.activities.ArticleActivity;
import com.codepath.newyorktimesearch.models.Article;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by klimjinx on 6/20/16.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    // Store a member variable for the articles
    private List<Article> mArticles;

    // Pass in the contact array into the constructor
    public ArticlesAdapter(List<Article> articles) {
        mArticles = articles;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the view within the item layout for the fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Holder contains member variable for any view that will be set as you render a row

        private Context context;
        // @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.rounded) SelectableRoundedImageView image;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context; // Store the context for itemClick
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Article article = mArticles.get(position);
            // Toast.makeText(context, article.getHeadline(), Toast.LENGTH_SHORT).show();

            // Create an intent to display article
            Intent intent = new Intent(context, ArticleActivity.class);
            // pass in that article to intent
            intent.putExtra("article", article);
            //launch the activity
            context.startActivity(intent);
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        return new ViewHolder(context, articleView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticlesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on the data model
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());
        // ImageView imageView = viewHolder.ivImage;
        // clear out recycled image from convertView from last time
//        imageView.setImageResource(0);

        // populate the thumbnail image & remote download image in the background
        String thumbnail = article.getThumbNail();

        // Used this for top corner rounding only
        SelectableRoundedImageView image = viewHolder.image;
        image.setImageResource(0);

        if (!TextUtils.isEmpty(thumbnail)) {
            // Set the height ratio before loading in image into Picasso
            Glide.with(image.getContext())
                    .load(thumbnail)
                    .into(image);
        } else {
            Glide.with(image.getContext())
                    .load(R.mipmap.ic_news)
                    .into(image);
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mArticles.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Article> articlesList) {
        mArticles.addAll(articlesList);
        notifyDataSetChanged();
    }



}
