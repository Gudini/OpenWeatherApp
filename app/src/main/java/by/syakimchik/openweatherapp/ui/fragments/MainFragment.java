package by.syakimchik.openweatherapp.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.loaders.WeatherInfoLoader;
import by.syakimchik.openweatherapp.loaders.adapters.CityAdapter;
import by.syakimchik.openweatherapp.ui.activity.MainActivity;


/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private CityAdapter mAdapter;

    //Time for search gps coordinates
    static final int GPS_TIME_SEARCH = 1000 * 60;

    private LocationManager mLocationManager;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) root.findViewById(R.id.cities_listview);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.blue, R.color.orange);

        mAdapter = new CityAdapter(getActivity(), null, 0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLoaderManager().initLoader( 0, null, this );

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isFirstTimeRun())
            runGpsSearch();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        return new CursorLoader(
                getContext(),
                CityTable.CONTENT_WEATHER_URI_BASE,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private boolean isFirstTimeRun() {
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.USER_SETTINGS, Context.MODE_PRIVATE);
        if (settings.getBoolean("is_first_time_run", true)) {
            settings.edit().putBoolean("is_first_time_run", false).commit();
            return true;
        }
        return false;
    }

    private void runGpsSearch() {
        ProgressDialog dialog = ProgressDialog.show(getActivity(), getString(R.string.search_coordinates), getString(R.string.please_waiting));
        dialog.setCancelable(true);

        final GPSLocationListener listener = new GPSLocationListener(dialog);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.stopLocationUpdateAndTimer(getString(R.string.search_is_canceled));
            }
        });

        if (hasGPSPermissions()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_SEARCH, 10,  listener);
        }
        else{
            dialog.dismiss();
        }
    }

    private boolean hasGPSPermissions(){
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment historyFragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.CITY_ID_ARG, id);
        bundle.putString(Constants.CITY_NAME, ((CityAdapter.ViewHolder) view.getTag()).cityName);
        historyFragment.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(historyFragment);
        ((MainActivity)getActivity()).startMode(MainActivity.Mode.HISTORY);
        ((MainActivity)getActivity()).setHomeButton(true);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.actions);
        builder.setItems(R.array.cities_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getContentResolver().delete(CityTable.CONTENT_ID_URI_BASE, CityTable.Columns.ID + "=?", new String[]{String.valueOf(id)});
            }
        });
        builder.create().show();
        return true;
    }


    private class GPSLocationListener implements LocationListener {

        protected Timer timerTimeout = new Timer();
        protected ProgressDialog dialog;

        public GPSLocationListener(ProgressDialog dialog){
            this.dialog = dialog;
            timerTimeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopLocationUpdateAndTimer(getString(R.string.location_is_not_found));
                }
            }, GPS_TIME_SEARCH, 1);
        }

        @Override
        public void onLocationChanged(Location location) {
            String cityName = null;
            String countryName = null;
            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(addresses!=null) {
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());
                    if (addresses.get(0) != null) {
                        cityName = addresses.get(0).getLocality();
                        countryName = addresses.get(0).getCountryName();
                    }
                }
            }
            catch (Exception e) {
                stopLocationUpdateAndTimer(getString(R.string.city_is_not_found));
                e.printStackTrace();
            }
            finally {
                if(cityName!=null)
                    stopLocationUpdateAndTimer(getString(R.string.city_is_found) + cityName + ", " + countryName);
                WeatherInfoLoader.loadWeatherByLocation(getContext(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


        private void stopLocationUpdateAndTimer(final String message) {
            if(hasGPSPermissions())
                mLocationManager.removeUpdates(this);

            if(timerTimeout!=null) {
                timerTimeout.cancel();
                timerTimeout.purge();
                timerTimeout = null;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void refreshData(){
        int count = mListView.getChildCount();
        StringBuilder sb = new StringBuilder();
        List<String> citiesId = new ArrayList<>();
        for (int i=0; i<count; i++) {
            CityAdapter.ViewHolder holder = ((CityAdapter.ViewHolder)mListView.getChildAt(i).getTag());
            sb.append(holder.cityCode+",");
            citiesId.add(String.valueOf(holder.cityId));
        }
        WeatherInfoLoader.updateCitiesData(getContext(), sb.toString(), citiesId, mSwipeRefreshLayout);
    }
}
