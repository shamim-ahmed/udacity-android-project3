package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.util.Constants;
import barqsoft.footballscores.util.Utilities;

public class FootballScoreIntentService extends IntentService {
    private static final String TAG = FootballScoreIntentService.class.getSimpleName();

    public FootballScoreIntentService() {
        super(FootballScoreIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context appContext = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(appContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(appContext, FootballScoreWidgetProvider.class));

        if (appWidgetIds == null || appWidgetIds.length == 0) {
            Log.i(TAG, "onHandleIntent called, but no widgets were updated");
            return;
        }

        for (int widgetId : appWidgetIds) {
            int layoutId = R.layout.widget_list_item;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            updateView(appContext, views);

            // configure what happens when the widget is clicked
            Intent appIntent = new Intent(appContext, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, appIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }

        Log.i(TAG, String.format("widgets with the following ids were updated: %s", Arrays.toString(appWidgetIds)));
    }

    private void updateView(Context context, RemoteViews views) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
        String dateStr = dateFormatter.format(new Date());
        Uri searchUri = DatabaseContract.ScoresTable.buildScoreWithDate();

        String sortOrder = String.format("%s, %s", DatabaseContract.ScoresTable.DATE_COL, DatabaseContract.ScoresTable.TIME_COL);
        Cursor cursor = context.getContentResolver().query(searchUri, null, null, new String[]{dateStr}, sortOrder);

        if (cursor == null) {
            Log.i(TAG, "No data found");
            return;
        }

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ContentValues values = readCursor(cursor);
            Log.i(TAG, "Retrieving data to be shown on the widget...");

            String homeGoals = values.getAsString(DatabaseContract.ScoresTable.HOME_GOALS_COL);

            if (Constants.INVALID_SCORE.equals(homeGoals)) {
                homeGoals = "";
            }

            String awayGoals = values.getAsString(DatabaseContract.ScoresTable.AWAY_GOALS_COL);

            if (Constants.INVALID_SCORE.equals(awayGoals)) {
                awayGoals = "";
            }

            String scoreStr = String.format("%s - %s", homeGoals, awayGoals);
            int homeIconResourceId = Utilities.getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.HOME_COL));
            int awayIconResourceId = Utilities.getTeamCrestByTeamName(values.getAsString(DatabaseContract.ScoresTable.AWAY_COL));

            views.setImageViewResource(R.id.home_crest, homeIconResourceId);
            views.setImageViewResource(R.id.away_crest, awayIconResourceId);
            views.setTextViewText(R.id.home_name, values.getAsString(DatabaseContract.ScoresTable.HOME_COL));
            views.setTextColor(R.id.home_name, Color.BLACK);
            views.setTextViewText(R.id.away_name, values.getAsString(DatabaseContract.ScoresTable.AWAY_COL));
            views.setTextColor(R.id.away_name, Color.BLACK);
            views.setTextViewText(R.id.score_textview, scoreStr);
            views.setTextColor(R.id.score_textview, Color.BLACK);
            views.setTextViewText(R.id.data_textview, values.getAsString(DatabaseContract.ScoresTable.TIME_COL));
            views.setTextColor(R.id.data_textview, Color.BLACK);
        }

        cursor.close();
    }

    private ContentValues readCursor(Cursor cursor) {
        ContentValues values = new ContentValues();
        int homeColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_COL);
        int awayColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_COL);
        int homeGoalsColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.HOME_GOALS_COL);
        int awayGoalsColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.AWAY_GOALS_COL);
        int timeColumnIndex = cursor.getColumnIndex(DatabaseContract.ScoresTable.TIME_COL);

        values.put(DatabaseContract.ScoresTable.HOME_COL, cursor.getString(homeColumnIndex));
        values.put(DatabaseContract.ScoresTable.AWAY_COL, cursor.getString(awayColumnIndex));
        values.put(DatabaseContract.ScoresTable.HOME_GOALS_COL, cursor.getString(homeGoalsColumnIndex));
        values.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL, cursor.getString(awayGoalsColumnIndex));
        values.put(DatabaseContract.ScoresTable.TIME_COL, cursor.getString(timeColumnIndex));

        return values;
    }
}
