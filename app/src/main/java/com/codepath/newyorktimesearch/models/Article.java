package com.codepath.newyorktimesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by klimjinx on 6/20/16.
 */
public class Article implements Parcelable {

    public String getWebURL() {
        return webURL;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    private String webURL;
    private String headline;
    private String thumbNail;

    public int describeContents() {
        return 0;
    }

    public Article(Parcel source) {
        this.webURL = source.readString();
        this.headline = source.readString();
        this.thumbNail = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(webURL);
        dest.writeString(headline);
        dest.writeString(thumbNail);
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.
            Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }
        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public Article(JSONObject object) {
        try {
            if( object.has("web_url")) {
                //it has it, do appropriate processing
                this.webURL = object.getString("web_url");
            } else if (object.has("url") ) {
                this.webURL = object.getString("url");
            }

            try {
                this.headline = object.getJSONObject("headline").getString("main");
            } catch (JSONException e)
            {
                try {
                    this.headline = object.getString("title");
                } catch (JSONException error) {
                    error.printStackTrace();
                }
            }

            JSONArray multimedia = object.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                int rand = new Random().nextInt(multimedia.length());
                JSONObject multimediaJSON = multimedia.getJSONObject(rand);

                if ( multimediaJSON.getString("url").startsWith("http") ) {
                    this.thumbNail = multimediaJSON.getString("url");
                } else {
                    this.thumbNail = "http://www.nytimes.com/" + multimediaJSON.getString("url");
                }
            } else {
                this.thumbNail = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public String toString() {
        return this.headline;
    }
}
