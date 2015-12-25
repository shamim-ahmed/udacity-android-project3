package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class MyFetchService extends IntentService
{
    public static final String LOG_TAG = MyFetchService.class.getSimpleName();
    public MyFetchService()
    {
        super(MyFetchService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        getData(getString(R.string.timeframe_next_two));
        getData(getString(R.string.timeframe_previous_two));

        return;
    }

    private void getData (String timeFrame)
    {
        //Creating fetch URL
        final String BASE_URL = getString(R.string.api_fixtures_base_url); //Base URL
        final String QUERY_TIME_FRAME = getString(R.string.timeframe_query_param); //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod(getString(R.string.http_get_method));
            m_connection.addRequestProperty(getString(R.string.auth_token_header_name), getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG,"Exception here" + e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray(getString(R.string.json_field_fixtures));
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }
    }
    private void processJSONdata (String JSONdata,Context context, boolean isReal)
    {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes

        final String BUNDESLIGA1 = getString(R.string.league_code_bundesliga1);
        final String BUNDESLIGA2 = getString(R.string.league_code_bundesliga2);
        final String PREMIER_LEAGUE = getString(R.string.league_code_premier);
        final String PRIMERA_DIVISION = getString(R.string.league_code_primera);
        final String SERIE_A = getString(R.string.league_code_serie_a);

        final String SEASON_LINK = getString(R.string.api_seasons_base_url);
        final String MATCH_LINK = getString(R.string.api_fixtures_base_url);
        final String FIXTURES = getString(R.string.json_field_fixtures);
        final String LINKS = getString(R.string.json_field_links);
        final String SOCCER_SEASON = getString(R.string.json_field_soccer_season);
        final String SELF = getString(R.string.json_field_self);
        final String MATCH_DATE = getString(R.string.json_field_match_date);
        final String HOME_TEAM = getString(R.string.json_field_home_team_name);
        final String AWAY_TEAM = getString(R.string.json_field_away_team_name);
        final String RESULT = getString(R.string.json_field_result);
        final String HOME_GOALS = getString(R.string.json_field_home_goals);
        final String AWAY_GOALS = getString(R.string.json_field_away_goals);
        final String MATCH_DAY = getString(R.string.json_field_match_day);

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++)
            {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString(getString(R.string.href_attribute_name));
                League = League.replace(SEASON_LINK, "");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if(     League.equals(PREMIER_LEAGUE)      ||
                        League.equals(SERIE_A)             ||
                        League.equals(BUNDESLIGA1)         ||
                        League.equals(BUNDESLIGA2)         ||
                        League.equals(PRIMERA_DIVISION)     )
                {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString(getString(R.string.href_attribute_name));
                    match_id = match_id.replace(MATCH_LINK, "");
                    if(!isReal){
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id=match_id+Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0,mDate.indexOf("T"));
                    // Fixed a bug: the locale must be specified here, otherwise the app will
                    // crash under a different locale
                    SimpleDateFormat match_date = new SimpleDateFormat(getString(R.string.date_format_long_1), Locale.US);
                    match_date.setTimeZone(TimeZone.getTimeZone(getString(R.string.time_zone)));
                    try {
                        Date parseddate = match_date.parse(mDate+mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat(getString(R.string.date_format_long_2), Locale.US);
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0,mDate.indexOf(":"));

                        if(!isReal){
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()+((i-2)*Constants.NUMBER_OF_MILLISECONDS_IN_DAY));
                            SimpleDateFormat mformat = new SimpleDateFormat(getString(R.string.date_format_short), Locale.US);
                            mDate=mformat.format(fragmentdate);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG,e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.ScoresTable.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.ScoresTable.DATE_COL,mDate);
                    match_values.put(DatabaseContract.ScoresTable.TIME_COL,mTime);
                    match_values.put(DatabaseContract.ScoresTable.HOME_COL,Home);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_COL,Away);
                    match_values.put(DatabaseContract.ScoresTable.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.ScoresTable.LEAGUE_COL,League);
                    match_values.put(DatabaseContract.ScoresTable.MATCH_DAY,match_day);
                    //log spam

                    //Log.v(LOG_TAG,match_id);
                    //Log.v(LOG_TAG,mDate);
                    //Log.v(LOG_TAG,mTime);
                    //Log.v(LOG_TAG,Home);
                    //Log.v(LOG_TAG,Away);
                    //Log.v(LOG_TAG,Home_goals);
                    //Log.v(LOG_TAG,Away_goals);

                    values.add(match_values);
                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = context.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI,insert_data);

            // broadcast the database change for the widgets
            final String actionDataUpdated = getString(R.string.action_data_updated);
            Intent dataUpdatedIntent = new Intent(actionDataUpdated).setPackage(getPackageName());
            context.sendBroadcast(dataUpdatedIntent);

            Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG,e.getMessage());
        }

    }
}

