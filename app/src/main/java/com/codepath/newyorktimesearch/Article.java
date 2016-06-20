package com.codepath.newyorktimesearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by klimjinx on 6/20/16.
 */
public class Article implements Serializable{

    public String getWebURL() {
        return webURL;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String webURL;
    String headline;
    String thumbNail;

    public Article(JSONObject object) {
        try {
            this.webURL = object.getString("web_url");
            this.headline = object.getJSONObject("headline").getString("main");

            JSONArray multimedia = object.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJSON = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" +  multimediaJSON.getString("url");

            } else {
                this.thumbNail = "";
            }

        } catch (JSONException e) {

        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<Article>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
