package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.util.Utilities;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
    private static final String TAG = ScoresAdapter.class.getSimpleName();

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;

    private final int positionToSelect;
    private boolean itemSelected = false;

    public ScoresAdapter(Context context, Cursor cursor, int flags, int positionToSelect)
    {
        super(context,cursor,flags);
        this.positionToSelect = positionToSelect;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.widget_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context pContext, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.score.setText(Utilities.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_HOME), pContext));
        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY), pContext));
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) pContext.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE), pContext));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(cursor.getInt(COL_LEAGUE), pContext));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            final String hashTag = pContext.getString(R.string.football_scores_hash_tag);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    pContext.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " ", hashTag));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }

    public View getView(int position, View convertView, ViewGroup parent){

        View item = super.getView(position, convertView, parent);
        item.clearFocus();

        if (positionToSelect != Constants.INVALID_SELECTED_INDEX && position == positionToSelect && !itemSelected) {
            if (parent instanceof  ListView) {
                Log.i(TAG, String.format("selecting position: %d", positionToSelect));
                ((ListView) parent).performItemClick(item, position, item.getId());
                itemSelected = true;
            }
        }

        return item;
    }
    public Intent createShareForecastIntent(String ShareText, String hashTag) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(mContext.getString(R.string.text_mime_type));
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + hashTag);
        return shareIntent;
    }

}
