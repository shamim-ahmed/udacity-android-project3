package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.service.MyFetchService;
import barqsoft.footballscores.util.Constants;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG = MainScreenFragment.class.getSimpleName();

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private ListView scoreList;
    private int positionToSelect = ListView.INVALID_POSITION;
    private boolean positionSelected = false;

    public MainScreenFragment()
    {
    }

    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }

    // the argument must be formatted as yyyy-MM-dd in US locale
    public void setFragmentDate(String date)
    {
        fragmentdate[0] = date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        scoreList = (ListView) rootView.findViewById(R.id.scores_list);

        Intent intent = getActivity().getIntent();
        positionToSelect = intent.getIntExtra(Constants.SELECTED_INDEX_ATTRIBUTE, ListView.INVALID_POSITION);
        intent.removeExtra(Constants.SELECTED_INDEX_ATTRIBUTE);
        Log.i(TAG, String.format("The position to select (as specified by the widget) is: %d", positionToSelect));

        mAdapter = new ScoresAdapter(getActivity(),null,0, positionToSelect);
        scoreList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.ScoresTable.buildScoreWithDate(),
                null,null,fragmentdate,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);

        if (scoreList != null && positionToSelect != ListView.INVALID_POSITION && !positionSelected) {
            scoreList.setSelection(positionToSelect);
            positionSelected = true;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }


}
