package com.codepath.newyorktimesearch;

import com.codepath.newyorktimesearch.models.Article;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * Created by klimjinx on 6/21/16.
 */
public class ArticlesResponse {
    ArrayList<Article> articles;

    // public constructor is necessary for collections
    public ArticlesResponse() {
        articles = new ArrayList<Article>();
    }

    public static ArticlesResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        ArticlesResponse articlesResponse = gson.fromJson(response, ArticlesResponse.class);
        return articlesResponse;
    }
}
