package com.codepath.newyorktimesearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.newyorktimesearch.Article;
import com.codepath.newyorktimesearch.ArticlesAdapter;
import com.codepath.newyorktimesearch.EndlessRecyclerViewScrollListener;
import com.codepath.newyorktimesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.btnSearch) Button btnSearch;
    @BindView(R.id.etQuery) EditText etQuery;

    // @BindView(R.id.gvResults) GridView gvResults;

    ArrayList<Article> articles;
    // ArticleArrayAdapter adapter;
    ArticlesAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        articles = new ArrayList<Article>();

        // Lookup the recyclerview in activity layout
        RecyclerView rvResults = (RecyclerView) findViewById(R.id.rvResults);

        // Initialize articles
        articles = new ArrayList<Article>();
        // Create adapter passing in the sample user data
        rvAdapter = new ArticlesAdapter(articles);
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(rvAdapter);


        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
// Attach the layout manager to the recycler view
        rvResults.setLayoutManager(gridLayoutManager);

        // Add the scroll listener
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });



        // adapter = new ArticleArrayAdapter(this, articles);
        // gvResults.setAdapter(adapter);
//        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Create an intent to display article
//                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
//
//                // get the article to display
//                Article article = articles.get(position);
//
//                // pass in that article to intent
//                intent.putExtra("article", article);
//
//                //launch the activity
//                startActivity(intent);
//            }
//        });

    }

    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.

        String query = etQuery.getText().toString();
        // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        params.put("page", offset);
        params.put("q", query);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    // adapter.addAll(Article.fromJSONArray(articleJSONResults));

                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();

                    // Deserialize response then construct new objects to append to the adapter
                    // Add the new objects to the data source for the adapter
                    articles.addAll(Article.fromJSONArray(articleJSONResults));

                    // For efficiency, notify the adapter of only the elements that got changed
                    // curSize equal to index of the first element inserted b/c list is 0-indexed
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                    Log.d("DEBUG", articles.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // hook up Listener using ButterKnife for grid click
//    @OnItemClick(R.id.gvResults)
//    public void onItemClick(AdapterView<?> parent, int position) {
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        params.put("page", 0);
        params.put("q", query);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    // adapter.addAll(Article.fromJSONArray(articleJSONResults));

                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                    Log.d("DEBUG", articles.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
