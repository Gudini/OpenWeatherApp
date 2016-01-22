package by.syakimchik.openweatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sergey on 11/1/2015.
 * @author Sergey Yakimchik
 */
public class CityWeather {

    @SerializedName("id")
    private String mCityCode;

    @SerializedName("name")
    private String mCityName;

    @SerializedName("main")
    private Temperature mTemp;

    @SerializedName("temp_max")
    private float mMaxTemp;

    @SerializedName("wind")
    private Wind mWind;

    @SerializedName("sys")
    private Country mCountry;

    @SerializedName("clouds")
    private Clouds mClouds;

    @SerializedName("weather")
    private List<Weather> mWeather;

    public String getDisplayCityName() {
        return mCityName+", "+mCountry.getName();
    }

    public String getCityName(){
        return mCityName;
    }

    public String getCountryName(){
        if(mCountry!=null)
            return mCountry.getName();
        return "";
    }

    public void setCityName(String cityName){
        mCityName = cityName;
    }

    public void setCountry(String country){
        mCountry = new Country();
        mCountry.setName(country);
    }

    public Temperature getTemperature(){
        return mTemp;
    }

    public Clouds getClouds() {
        return mClouds;
    }

    public String getWeatherMain(){
        if(mWeather!=null){
            for(Weather weather: mWeather)
            {
                return weather.getMain();
            }
        }
        return "";
    }

    public String getWeatherDescription(){
        if(mWeather!=null){
            for(Weather weather: mWeather)
            {
                return weather.getDescription();
            }
        }
        return "";
    }

    public Wind getWind() {
        return mWind;
    }

    public String getCityCode() {
        return mCityCode;
    }

    public void setCityCode(String cityCode){
        mCityCode = cityCode;
    }
}
