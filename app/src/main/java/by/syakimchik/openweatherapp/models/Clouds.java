package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class Clouds {

    @SerializedName("all")
    private float mClouds;

    public float getClouds() {
        return mClouds;
    }
}
