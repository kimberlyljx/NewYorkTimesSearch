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

import com.codepath.newyorktimesearch.ArticlesAdapter;
import com.codepath.newyorktimesearch.EndlessRecyclerViewScrollListener;
import com.codepath.newyorktimesearch.R;
import com.codepath.newyorktimesearch.SpacesItemDecoration;
import com.codepath.newyorktimesearch.fragments.EditSettingDialogFragment;
import com.codepath.newyorktimesearch.models.Article;
import com.codepath.newyorktimesearch.models.Setting;
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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity  implements EditSettingDialogFragment.EditSettingDialogListener {

    // @BindView(R.id.gvResults) GridView gvResults;
    MenuItem searchItem;

    String query = "";
    ArrayList<Article> articles;
    ArticlesAdapter rvAdapter;
    StaggeredGridLayoutManager gridLayoutManager;
    Setting currentSettings;
    String searchURL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String topURL = "https://api.nytimes.com/svc/topstories/v2/home.json";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        this.currentSettings = new Setting();
        articles = new ArrayList<Article>();
        rvResults.setItemAnimator(new SlideInUpAnimator());
        // Initialize articles
        articles = new ArrayList<Article>();
        // Create adapter passing in the sample user data
        rvAdapter = new ArticlesAdapter(articles);
        // Attach the adapter to the recycler view to populate items
        rvResults.setAdapter(rvAdapter);

        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        rvResults.addItemDecoration(decoration);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // code to refresh the list here.
                fetchTimelineAsync(0); // always refresh from page 0
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

        // set to None to prevent shuffling
        gridLayoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        // Attach the layout manager to the recycler view
        rvResults.setLayoutManager(gridLayoutManager);

        // Add the scroll listener
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(false, page);
            }
        });
        displayTop();
    }

    public void apiCall(final boolean clearAdapter, final boolean refreshing, String URL, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("SEARCH_ACTIVITY", URL + "?" + params);
        client.get(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJSONResults = null;
                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");

                    if (clearAdapter) {
                        rvAdapter.clear();
                    }

                    // record this value before making any changes to the existing list
                    int curSize = rvAdapter.getItemCount();
                    // Deserialize response then construct new objects to append to the adapter
                    // Add the new objects to the data source for the adapter
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    // For efficiency, notify the adapter of only the elements that got changed
                    // curSize equal to index of the first element inserted b/c list is 0-indexed
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);

                    if (rvAdapter.getItemCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Sorry, no matches. " +
                                "Try setting less filters", Toast.LENGTH_LONG).show();
                    }
                    Log.d("DEBUG", articles.toString());
                    if (refreshing) {
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false);
                    }

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

    public void fetchTimelineAsync(int page) {
        if (query.contentEquals("")) {
            Toast.makeText(this, "Displaying top pages", Toast.LENGTH_SHORT).show();
            displayTop();
            swipeContainer.setRefreshing(false);
        } else {
            Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
            RequestParams params = giveParams(currentSettings, page);
            apiCall(true, true, searchURL, params);
        }
    }


    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(boolean clearAdapter, int offset) {

        if (query.contentEquals("")) {
            // displayTop(); // topnews does not accept page offset
            return;
        }
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        RequestParams params = giveParams(currentSettings, offset);
        apiCall(clearAdapter, false, searchURL, params);
    }

    public void displayTop() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        Log.d("SEARCH_ACTIVITY", topURL + "?" + params);
        client.get(topURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJSONResults = null;
                try {
                    articleJSONResults = response.getJSONArray("results");
                    rvAdapter.clear();
                    int curSize = rvAdapter.getItemCount();
                    articles.addAll(Article.fromJSONArray(articleJSONResults));
                    rvAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                rvAdapter.clear();
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

                Toast.makeText(getApplicationContext(), "Searching for " + search_query, Toast.LENGTH_SHORT).show();
                query = search_query;
                RequestParams params = giveParams(currentSettings, 0);
                apiCall(true, false, searchURL, params);

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
        return super.onOptionsItemSelected(item);
    }

    // DIALOG
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        // way to persist data
        EditSettingDialogFragment editSettingDialogFragment = EditSettingDialogFragment.newInstance(currentSettings);
//                currentSettings.spinnerIndex, filterArts, filterMagazines, filterMovies, beginDate);
        editSettingDialogFragment.show(fm, "fragment_edit_setting");
    }

    public RequestParams giveParams (Setting setting, int page) {
        RequestParams params = new RequestParams();
        params.put("api-key", "18d9a8651d754a6883f1b4c72b55da4c");
        params.put("page", page);
        params.put("q", query);

        // Set Date
        if (setting.beginDate != "" ) {
            params.put("begin_date",setting.beginDate.replaceAll("-", ""));
        }
        switch (setting.spinnerIndex) {
            case 1:
                params.put("sort", "newest");
                break;
            case 2:
                params.put("sort", "oldest");
                break;
            default:
                break;
        }

        // Filter to do: I hate myself for this ugly nested if else
        if (currentSettings.filterMagazines) {
            if (currentSettings.filterArts) {
                if (currentSettings.filterMovies) {
                    params.put("fq", "news_desk:(\"Magazine\" \"Arts&Leisure\" \"Movies\")");
                } else {
                    params.put("fq", "news_desk:(\"Magazine\" \"Arts&Leisure\")");
                }

            } else {
                if (currentSettings.filterMovies) {
                    params.put("fq", "news_desk:(\"Magazine\" \"Movies\")");
                } else {
                    params.put("fq", "news_desk:(\"Magazine\")");
                }
            }
        } else {
            if (currentSettings.filterArts) {
                if (currentSettings.filterMovies) {
                    params.put("fq", "news_desk:(\"Arts&Leisure\" \"Movies\")");
                } else {
                    params.put("fq", "news_desk:(\"Arts&Leisure\")");
                }

            } else {
                if (currentSettings.filterMovies) {
                    params.put("fq", "news_desk:(\"Movies\")");
                }
            }
        }
        return params;
    }

    // 3. This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    @Override
    public void onFinishEditDialog(Setting newSettings) {
        // Check whether top stories
        currentSettings = newSettings;

        if (query.contentEquals("") ) {
            Toast.makeText(this, "Please enter query", Toast.LENGTH_SHORT).show();
            return; // don't refresh if on top pges
        } else {
            Toast.makeText(this, "Applying filters", Toast.LENGTH_SHORT).show();
            RequestParams params = giveParams(currentSettings, 0);
            apiCall(true, false, searchURL, params);
        }
    }

    // When dialog is cancelled, return to original settings
    @Override
    public void onFinishEditDialog(String cancel, Setting oldSetting) {
        currentSettings = oldSetting;
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
//        // Check which checkbox was clicked
//        switch(view.getId()) {
//            case R.id.cbMagazine:
//                if (checked) {
//                    Toast.makeText(this, "Filtering for Magazines", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.cbArtsAndLeisure:
//                if (checked) {
//                    Toast.makeText(this, "Filtering for Arts", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.cbMovies:
//                if (checked) {
//                    Toast.makeText(this, "Filtering for Movies", Toast.LENGTH_SHORT).show();
//                }
//        }
    }

}
