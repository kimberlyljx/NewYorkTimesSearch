package com.codepath.newyorktimesearch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.codepath.newyorktimesearch.Article;
import com.codepath.newyorktimesearch.ArticlesAdapter;
import com.codepath.newyorktimesearch.EditSettingDialogFragment;
import com.codepath.newyorktimesearch.EndlessRecyclerViewScrollListener;
import com.codepath.newyorktimesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity  implements EditSettingDialogFragment.EditSettingDialogListener {

    // @BindView(R.id.gvResults) GridView gvResults;
    MenuItem searchItem;
    private SwipeRefreshLayout swipeContainer;
    String query;
    ArrayList<Article> articles;
    ArticlesAdapter rvAdapter;
    RecyclerView rvResults;
    StaggeredGridLayoutManager gridLayoutManager;

    boolean noFilter;
    boolean filterMovies;
    boolean filterArts;
    boolean filterMagazines;
    int spinnerIndex; // 0 is newest, 1 is oldest

    // Check for any filter applied
    public void checkForFilter() {
        if (!(filterMagazines || filterArts || filterMovies)) {
            noFilter = false;
        } else {
            noFilter = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        articles = new ArrayList<Article>();

        // Lookup the recycler view in activity layout
        rvResults = (RecyclerView) findViewById(R.id.rvResults);

        // Initialize articles
        articles = new ArrayList<Article>();
        // Create adapter passing in the sample user data
        rvAdapter = new ArticlesAdapter(articles);
        // Attach the adapter to the recycler view to populate items
        rvResults.setAdapter(rvAdapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // code to refresh the list here.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

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
        displayTop();
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP

        //String query = etQuery.getText().toString();
//        String query = SearchManager.QUERY;
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();

        String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        params.put("page", page);
        params.put("q", query);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    // Remember to CLEAR OUT old items before appending in the new ones
                    rvAdapter.clear();
                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();

                    // Deserialize response then construct new objects to append to the adapter
                    // Add the new objects to the data source for the adapter
                    articles.addAll(Article.fromJSONArray(articleJSONResults));

                    // For efficiency, notify the adapter of only the elements that got changed
                    // curSize equal to index of the first element inserted b/c list is 0-indexed
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);

                    Log.d("DEBUG", articles.toString());
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);

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


    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
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

    public void displayTop() {
        String URL = "https://api.nytimes.com/svc/topstories/v2/home.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONArray("results");
                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();
                    // Deserialize response then construct new objects to append to the adapter
                    // Add the new objects to the data source for the adapter
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    // For efficiency, notify the adapter of only the elements that got changed
                    // curSize equal to index of the first element inserted b/c list is 0-indexed
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);

                    Log.d("DEBUG", articles.toString()  );

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

        // Read the scroll listener
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String search_query) {
                // perform query here ONLY if different query
                if (!search_query.equalsIgnoreCase(query)) {
                    Toast.makeText(getApplicationContext(), search_query, Toast.LENGTH_SHORT).show();
                    query = search_query;
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
                                // Remember to CLEAR OUT old items before appending in the new ones
                                rvAdapter.clear();
                                // record this value before making any changes to the existing list
                                int curSize = rvAdapter.getItemCount();
                                // Deserialize response then construct new objects to append to the adapter
                                // Add the new objects to the data source for the adapter
                                articles.addAll(Article.fromJSONArray(articleJSONResults));
                                // For efficiency, notify the adapter of only the elements that got changed
                                // curSize equal to index of the first element inserted b/c list is 0-indexed
                                rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);

                                // Log.d("DEBUG", articles.toString());
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

                    // Read the scroll listener
                    rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            customLoadMoreDataFromApi(page);
                        }
                    });

                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // return true;
            showEditDialog();
        }
//
//        switch (id) {
//            case android.R.id.home:
//                break;
//            case R.id.menuitem1:
//                Toast.makeText(this, "Menu Item 1", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }

        return super.onOptionsItemSelected(item);
    }

    // DIALOG
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();

        // way to persist data
        EditSettingDialogFragment editSettingDialogFragment = EditSettingDialogFragment.newInstance(spinnerIndex, filterArts, filterMagazines, filterMovies);
        editSettingDialogFragment.show(fm, "fragment_edit_setting");
    }


    // 3. This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    @Override
    public void onFinishEditDialog(int spinnerIndex, boolean filterArt, boolean filterMagazine, boolean filterMovies) {
        // Toast.makeText(this, "Searching for " + filterArt, Toast.LENGTH_SHORT).show();

        String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        params.put("page", 0);
        params.put("q", query); // query should be the same

        String sort = "newest"; // by default
        if (spinnerIndex == 1) {
            sort = "oldest";
        }

        params.put("sort",sort); // query should be the same

        // filter TO DO
        if (filterMagazine) {
            params.put("fq", "news_desk:(\"Magazine\")");
        }

        if (filterArt) {
            // figure how to parse with multiple selections
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJSONResults = null;

                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    // Remember to CLEAR OUT old items before appending in the new ones
                    rvAdapter.clear();
                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();
                    // Deserialize response then construct new objects to append to the adapter
                    // Add the new objects to the data source for the adapter
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    // For efficiency, notify the adapter of only the elements that got changed
                    // curSize equal to index of the first element inserted b/c list is 0-indexed
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);

                    Log.d("DEBUG_DIALOG", articles.toString()  );

                } catch (JSONException e) {
                    Log.d("DEBUG_DIALOG", "FAILED"  );
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

    // When dialog is cancelled, return to original settings
    @Override
    public void onFinishEditDialog(String cancel, int spinnerIndex, boolean filterArt, boolean filterMagazine, boolean filterMovies) {
        this.filterArts = filterArt;
        this.filterMagazines = filterMagazine;
        this.filterMovies = filterMovies;
        this.spinnerIndex = spinnerIndex;
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cbMagazine:
                if (checked) {
                    Toast.makeText(this, "Filtering for Magazines", Toast.LENGTH_SHORT).show();
                    filterMagazines = true;
                } else {
                    filterMagazines = false;
                }
                break;
            case R.id.cbArtsAndLeisure:
                if (checked) {
                    filterArts = true;
                    Toast.makeText(this, "Filtering for Arts", Toast.LENGTH_SHORT).show();
                } else {
                    filterArts = false;
                }
                break;
            case R.id.cbMovies:
                if (checked) {
                    Toast.makeText(this, "Filtering for Movies", Toast.LENGTH_SHORT).show();
                    filterMovies = true;
                } else {
                    filterMovies = false;
                }
        }
    }

}
