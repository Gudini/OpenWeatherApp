package com.quinny898.library.persistentsearch;

import android.graphics.drawable.Drawable;

public class SearchResult {
    public String title;
    public Drawable icon;
    public String cityCode;

    /**
     * Create a search result with text and an icon
     * @param title
     * @param icon
     */
    public SearchResult(String title, Drawable icon, String cityCode) {
       this.title = title;
       this.icon = icon;
        this.cityCode = cityCode;
    }

    public int viewType = 0;

    public SearchResult(String title){
        this.title = title;
    }

    public SearchResult(int viewType, String title){
        this.viewType = viewType;
        this.title = title;
    }
    
    /**
     * Return the title of the result
     */
    @Override
    public String toString() {
        return title;
    }

    public String getCityCode(){
        return cityCode;
    }
    
}