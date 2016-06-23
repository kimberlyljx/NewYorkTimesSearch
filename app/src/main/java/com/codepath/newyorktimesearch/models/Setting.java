package com.codepath.newyorktimesearch.models;

import org.parceler.Parcel;

/**
 * Created by klimjinx on 6/22/16.
 */

@Parcel
public class Setting {

    // fields must be public
    public String beginDate = "";
    public boolean filterMovies = false;
    public boolean filterArts = false;
    public boolean filterMagazines = false;
    public int spinnerIndex = 0;
    // 0 is none, 1 is newest, 2 is oldest

    // empty constructor needed by the Parceler library
    public Setting() {
    }

    public Setting(String beginDate, boolean filterArts,  boolean filterMagazines, boolean filterMovies, int spinnerIndex) {
        this.beginDate = beginDate;
        this.filterArts = filterArts;
        this.filterMagazines = filterMagazines;
        this.filterMovies = filterMovies;
        this.spinnerIndex = spinnerIndex;
    }

}
