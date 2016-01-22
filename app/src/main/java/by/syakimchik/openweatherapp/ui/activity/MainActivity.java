package by.syakimchik.openweatherapp.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchBox.SearchListener;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.List;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.api.ApiFactory;
import by.syakimchik.openweatherapp.api.WeatherInfoService;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.loaders.AutoUpdateService;
import by.syakimchik.openweatherapp.loaders.WeatherInfoLoader;
import by.syakimchik.openweatherapp.models.CityWeather;
import by.syakimchik.openweatherapp.models.WeatherInfo;
import by.syakimchik.openweatherapp.ui.fragments.MainFragment;
import by.syakimchik.openweatherapp.ui.fragments.SettingsFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by Sergey on 11/1/2015.
 * @author Sergey Yakimchik
 */
public class MainActivity extends AppCompatActivity {

    public enum Mode {
        NORMAL, HISTORY, SEARCH, SETTINGS
    }

    public static Mode sMode;

    private Toolbar mActionBarToolbar;

    private SearchBox mSearchBox;

    private MenuItem mSearchMenuItem;
    private MenuItem mSettingsMenuItem;

    private AutoUpdateService mUpdateService;
    private ServiceConnection mConnection;
    private boolean isBound;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mSearchBox = (SearchBox) findViewById(R.id.searchbox);
        replaceFragment(new MainFragment());

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                mUpdateService = ((AutoUpdateService.UpdateBinder) binder).getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;
                mUpdateService = null;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIntent = new Intent(this, AutoUpdateService.class);
        startService(mIntent);
        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isBound) return;
        unbindService(mConnection);
        isBound = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSettingsMenuItem = menu.findItem(R.id.action_settings);
        startMode(Mode.NORMAL);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (sMode != Mode.NORMAL) {
            startMode(Mode.NORMAL);
            getSupportFragmentManager().popBackStack();
            setHomeButton(false);
        } else {
            finish();
        }
    }

    public void startMode(Mode modeToStart) {
        if (modeToStart == Mode.NORMAL) {
            setOptionsMenuVisibility(true);
            setActionBarTitle(getString(R.string.app_name));
        }
        else if (modeToStart == Mode.SETTINGS) {
            setOptionsMenuVisibility(false);
            setActionBarTitle(getString(R.string.action_settings));
        }
        else if (modeToStart == Mode.SEARCH || modeToStart == Mode.HISTORY) {
            setOptionsMenuVisibility(false);
        }

        sMode = modeToStart;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                replaceFragment(new SettingsFragment());
                startMode(Mode.SETTINGS);
                setHomeButton(true);
                return true;
            case R.id.action_search:
                openSearch();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openSearch() {
        setActionBarTitle("");
        getSupportFragmentManager().findFragmentById(R.id.content_frame).getView().setVisibility(View.GONE);
        mSearchBox.revealFromMenuItem(R.id.action_search, this);

        mSearchBox.setSearchListener(new SearchListener() {

            @Override
            public void onSearchOpened() {
                mSearchBox.clearResults();
                startMode(Mode.SEARCH);
            }

            @Override
            public void onSearchClosed() {
                // Use this to un-tint the screen
                closeSearch();
                startMode(Mode.NORMAL);
            }

            @Override
            public void onSearchTermChanged(String term) {
                if (term.length() >= 3) {
                    WeatherInfoService service = ApiFactory.getWeatherInfoService();
                    Call<WeatherInfo> call = service.citiesInfo(term, Constants.APPID);
                    mSearchBox.showLoading(true);
                    call.enqueue(new Callback<WeatherInfo>() {
                        @Override
                        public void onResponse(Response<WeatherInfo> response) {
                            mSearchBox.showLoading(false);
                            if (response.isSuccess()) {
                                WeatherInfo weatherInfo = response.body();
                                if (weatherInfo != null) {
                                    List<CityWeather> infoList = weatherInfo.getInfo();
                                    if (infoList != null) {
                                        mSearchBox.clearSearchable();
                                        for (int i = 0; i < infoList.size(); i++) {
                                            SearchResult option = new SearchResult(infoList.get(i).getDisplayCityName(), ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_history), infoList.get(i).getCityCode());
                                            mSearchBox.addSearchable(option);
                                            mSearchBox.updateResults();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            mSearchBox.showLoading(false);
                        }
                    });
                }
            }

            @Override
            public void onSearch(String searchTerm) {
                Toast.makeText(MainActivity.this, getString(R.string.warning_change_city),
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void onResultClick(SearchResult result) {
                CityWeather city = new CityWeather();
                final String[] parts = result.toString().split(",");
                city.setCityName(parts[0]);
                city.setCountry(parts[1].replace(" ", ""));
                city.setCityCode(result.getCityCode());
                String cityId = CityTable.save(getApplicationContext(), city);
                WeatherInfoLoader.loadData(getApplicationContext(), parts[0], cityId);
                mSearchBox.clearSearchText();
            }

            @Override
            public void onBackClick() {
                if(sMode!=Mode.NORMAL) {
                    startMode(Mode.NORMAL);
                    closeSearch();
                }
            }

            @Override
            public void onSearchCleared() {
            }

        });

    }

    protected void closeSearch() {
        mSearchBox.hideCircularly(this);
        getSupportFragmentManager().findFragmentById(R.id.content_frame).getView().setVisibility(View.VISIBLE);
    }

    public void setActionBarTitle(String title){
        mActionBarToolbar.setTitle(title);
    }

    public void replaceFragment(Fragment fragment){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setHomeButton(boolean visibility){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(visibility);
            actionBar.setDisplayHomeAsUpEnabled(visibility);
        }
    }

    private void setOptionsMenuVisibility(boolean visibility){
        mSearchMenuItem.setVisible(visibility);
        mSettingsMenuItem.setVisible(visibility);
    }

    public String getTempVar(){
        SharedPreferences sharedPref = getSharedPreferences(Constants.USER_SETTINGS, Context.MODE_PRIVATE);
        switch (sharedPref.getInt(Constants.TEMP_POSITION, 0)){
            case 0:
            default:
                return "C";
            case 1:
                return "K";
            case 2:
                return "F";
        }
    }

    public void updateServiceInterval(){
        mUpdateService.updateInterval();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
