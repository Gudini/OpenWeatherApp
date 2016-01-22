package by.syakimchik.openweatherapp.loaders;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.api.ApiFactory;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.database.tables.WeatherTable;
import by.syakimchik.openweatherapp.models.CityWeather;
import by.syakimchik.openweatherapp.models.WeatherInfo;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Sergey on 11/1/2015.
 * @author Sergey Yakimchik
 */
public class WeatherInfoLoader{

    public static void loadData(final Context context, String cityName, final String cityId){
        Call<CityWeather> call = ApiFactory.getWeatherInfoService().cityWeather(cityName, Constants.UNITS_CELSIUS, Constants.APPID);
        call.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Response<CityWeather> response) {
                if(response.isSuccess()){
                    WeatherTable.save(context, response.body(), cityId);
                    context.getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public static void updateCitiesData(final Context context, String citiesCode, final List<String> citiesId, final SwipeRefreshLayout layout){
        Call<WeatherInfo> call = ApiFactory.getWeatherInfoService().updateCitiesInfo(citiesCode, Constants.UNITS_CELSIUS, Constants.APPID);
        call.enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Response<WeatherInfo> response) {
                if(layout!=null)
                    layout.setRefreshing(false);
                if(response.isSuccess()){
                    WeatherTable.save(context, response.body().getInfo(), citiesId);
                    context.getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if(layout!=null)
                    layout.setRefreshing(false);
            }
        });
    }

    public static void loadWeatherByLocation(final Context context, String lat, String lon){
        Call<CityWeather> call = ApiFactory.getWeatherInfoService().cityWeatherByLocation(lat, lon, Constants.UNITS_CELSIUS, Constants.APPID);
        call.enqueue(new Callback<CityWeather>() {
            @Override
            public void onResponse(Response<CityWeather> response) {
                if(response.isSuccess()){
                    String cityId = CityTable.save(context, response.body());
                    WeatherTable.save(context, response.body(), cityId);
                    context.getContentResolver().notifyChange(CityTable.CONTENT_WEATHER_URI_BASE, null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}
