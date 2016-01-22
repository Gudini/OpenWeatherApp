package by.syakimchik.openweatherapp.api;

import by.syakimchik.openweatherapp.models.CityWeather;
import by.syakimchik.openweatherapp.models.WeatherInfo;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Sergey on 11/1/2015.
 * @author Sergey Yakimchik
 */
public interface WeatherInfoService {

    @GET("/data/2.5/find")
    Call<WeatherInfo> citiesInfo(@Query("q") String cityName, @Query("APPID") String appId);

    @GET("/data/2.5/weather")
    Call<CityWeather> cityWeather(@Query("q") String cityName, @Query("units") String units, @Query("APPID") String appId);

    @GET("/data/2.5/group")
    Call<WeatherInfo> updateCitiesInfo(@Query("id") String citiesCode, @Query("units") String units, @Query("APPID") String appId);

    @GET("/data/2.5/weather")
    Call<CityWeather> cityWeatherByLocation(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String units, @Query("APPID") String appId);
}
