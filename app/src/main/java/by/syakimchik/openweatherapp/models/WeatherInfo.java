package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class WeatherInfo {

    @SerializedName("message")
    private String mMessage;

    @SerializedName("list")
    private List<CityWeather> mInfo;

    public List<CityWeather> getInfo() {
        return mInfo;
    }
}
