package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class Weather {

    @SerializedName("main")
    private String mMain;

    @SerializedName("description")
    private String mDescription;

    public String getMain() {
        return mMain;
    }

    public String getDescription() {
        return mDescription;
    }
}
