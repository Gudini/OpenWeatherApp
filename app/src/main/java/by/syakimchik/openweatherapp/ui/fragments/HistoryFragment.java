package by.syakimchik.openweatherapp.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import by.syakimchik.openweatherapp.Constants;
import by.syakimchik.openweatherapp.R;
import by.syakimchik.openweatherapp.database.tables.CityTable;
import by.syakimchik.openweatherapp.loaders.adapters.HistoryAdapter;
import by.syakimchik.openweatherapp.ui.activity.MainActivity;

/**
 * Created by Sergey on 10/31/2015.
 * @author Sergey Yakimchik
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private long mCityId;

    private HistoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        Bundle bundle = getArguments();
        mCityId = bundle.getLong(Constants.CITY_ID_ARG);

        mAdapter = new HistoryAdapter(getActivity(), null, 0);
        ListView mListView = (ListView) root.findViewById(R.id.history_listview);
        mListView.setAdapter(mAdapter);

        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.action_history) + bundle.getString(Constants.CITY_NAME));

        getLoaderManager().initLoader( 0, null, this );
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getContext(),
                CityTable.CONTENT_HISTORY_WEATHER_URI_BASE,
                null,
                CityTable.Requests.TABLE_NAME+"."+CityTable.Columns.ID+"=?",
                new String[]{String.valueOf(mCityId)},
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
}
