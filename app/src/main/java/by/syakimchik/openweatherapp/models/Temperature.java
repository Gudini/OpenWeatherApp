package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class Temperature {

    @SerializedName("temp")
    private float mTemp;

    @SerializedName("temp_min")
    private float mMinTemp;

    @SerializedName("temp_max")
    private float mMaxTemp;

    public float getTemp() {
        return mTemp;
    }

    public float getMinTemp() {
        return mMinTemp;
    }

    public float getMaxTemp() {
        return mMaxTemp;
    }
}
