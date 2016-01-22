package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class Country {

    @SerializedName("country")
    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
